package ch.njol.skript.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.*;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.github.hapily04.skriptminestom.util.FileUtils;
import com.github.hapily04.skriptminestom.util.MiniRegionFile;
import net.hollowcube.polar.PolarChunk;
import net.hollowcube.polar.PolarLoader;
import net.minestom.server.MinecraftServer;
import net.minestom.server.ServerFlag;
import net.minestom.server.event.instance.InstanceChunkLoadEvent;
import net.minestom.server.instance.*;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.Generator;
import net.minestom.server.registry.RegistryKey;
import net.minestom.server.world.DimensionType;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.eclipse.jdt.annotation.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.util.LiteralEntryData;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

@Name("Create Instance")
@Description("Creates a new instance container or a shared instance.")
@Examples({
	"create instance and store it in {_instance}:",
	"    file: \"worlds/lobby\"",
	"    loader: \"anvil\"",
	"    generator:",
	"        fill chunk with stone",
	"    preload option: \"normal\""
})
public class EffSecCreateInstance extends EffectSection {

	private static final EntryValidator ENTRY_VALIDATOR;
	private static final List<String> VALID_LOADER_ENTRIES = List.of("anvil", "polar");
	// maybe a preload-super-strict option that only loads chunks w blocks in them instead of every chunk that the world provides?
	private static final List<String> VALID_GENERATOR_PRESET_ENTRIES = List.of("strict", "normal");
	private static final Generator BLANK_GENERATOR = unit -> unit.modifier().fill(Block.AIR);
	private static final List<Instance> RELIGHT_INSTANCES = new ArrayList<>();

	static {
		ENTRY_VALIDATOR = EntryValidator.builder()
										.addEntryData(new LiteralEntryData<>("dynamic lighting", null, true, Boolean.class))
										.addEntry("file", null, true)
										.addEntry("loader", null, true)
										//.addEntryData(new ExpressionEntryData<>("dimension", null, true, DimensionType.class))
										.addSection("generator", true)
										.addEntry("preload option", null, true)
										.build();
		Skript.registerSection(EffSecCreateInstance.class,
			"create instance [container] (and store it|stored) in %objects%",
			"create shared instance from [instance] %instancecontainer% (and store it|stored) in %objects%");

		MinecraftServer.getGlobalEventHandler().addListener(InstanceChunkLoadEvent.class, event -> {
			Instance instance = event.getInstance();
			if (!RELIGHT_INSTANCES.contains(instance)) return;
			LightingChunk.relight(instance, List.of(event.getChunk()));
		});
	}

	private int matchedPattern;
	@SuppressWarnings("NotNullFieldNotInitialized")
	private Expression<Object> storage;
	@Nullable
	private Expression<InstanceContainer> originalInstance;
	private boolean dynamicLighting = false;
	@Nullable
	private File worldFile;
	@Nullable
	private String loader;
	@Nullable
	private Expression<DimensionType> dimension;
	@Nullable
	private String preloadOption;
	@Nullable
	private Trigger generator;
	private boolean phonyAnvilLoader = false;
	// todo time & time rate of world
	@SuppressWarnings({"NullableProblems", "unchecked", "ConstantValue"})
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult,
						SectionNode sectionNode, List<TriggerItem> triggerItems) {
		this.matchedPattern = matchedPattern;
		if (!isDelayed.isFalse()) {
			Skript.error("An instance creation section cannot be delayed.");
			return false;
		}
		if (sectionNode != null) {
			if (matchedPattern == 1) {
				Skript.error("A shared instance cannot have a loader, generator, or dimension. It copies everything except for entities from its original instance.");
				return false;
			}
			EntryContainer container = ENTRY_VALIDATOR.validate(sectionNode);
			if (container == null) return false; // shouldn't be null because section node isn't null

			Boolean dynamicLighting = container.getOptional("dynamic lighting", Boolean.class, false);
			if (dynamicLighting != null) this.dynamicLighting = dynamicLighting;

			String worldFile = container.getOptional("file", String.class, false);
			this.worldFile = worldFile == null ? null : new File(FileUtils.getServerDirectory(), worldFile);

			String loader = container.getOptional("loader", String.class, false);
			if (loader != null) {
				if (this.worldFile == null) {
					Skript.error("If a loader is provided, a valid storage location must also be provided (file is missing).");
					return false;
				}
				if (!VALID_LOADER_ENTRIES.contains(loader)) {
					Skript.error("An invalid chunk loader has been provided: '" + loader + "'.");
					return false;
				}
				this.loader = loader;
			}

			this.dimension = container.getOptional("dimension", Expression.class, false);

			String preloadOption = container.getOptional("preload option", String.class, false);
			if (preloadOption != null) {
				if (!VALID_GENERATOR_PRESET_ENTRIES.contains(preloadOption)) {
					Skript.error("An invalid preload option has been provided: '" + preloadOption + "'.");
					return false;
				}
				this.preloadOption = preloadOption;
			}

			SectionNode generator = container.getOptional("generator", SectionNode.class, false);
			if (generator != null) this.generator = loadCode(generator, "generator", TerrainGenerateEvent.class);
		}
		// only store expressions if it's successfully initialized
		if (matchedPattern == 0) storage = (Expression<Object>) expressions[0];
		else {
			originalInstance = (Expression<InstanceContainer>) expressions[0];
			storage = (Expression<Object>) expressions[1];
		}
		return true;
	}

	@Override
	protected @Nullable TriggerItem walk(Event event) {
		Instance instance;
		if (matchedPattern == 0) {
			if (dimension != null) {
				DimensionType dimension = this.dimension.getSingle(event);
				if (dimension == null) {
					Skript.warning("Dimension provided, but its value is none. Creating base instance container.");
					instance = MinecraftServer.getInstanceManager().createInstanceContainer();
				} else {
					RegistryKey<DimensionType> dimensionKey = MinecraftServer.getDimensionTypeRegistry().getKey(dimension);
					if (dimensionKey == null) {
						Skript.warning("Dimension provided, but it doesn't seem like it was registered? Creating base instance container.");
						instance = MinecraftServer.getInstanceManager().createInstanceContainer();
					} else instance = MinecraftServer.getInstanceManager().createInstanceContainer(dimensionKey);
				}
			} else {
				phonyAnvilLoader = true;
				instance = MinecraftServer.getInstanceManager().createInstanceContainer(new AnvilLoader(UUID.randomUUID().toString()));
			}

			InstanceContainer container = (InstanceContainer) instance;
			if (dynamicLighting) {
				instance.setChunkSupplier(LightingChunk::new);
				RELIGHT_INSTANCES.add(container);
			}
			assert worldFile != null; // shouldn't be null because we throw a skript error if the file doesn't exist or if it's null
			if (loader != null) {
				Path worldPath = worldFile.toPath();
				if (loader.equalsIgnoreCase("anvil")) container.setChunkLoader(new AnvilLoader(worldPath));
				else {
					try {
						PolarLoader loader = new PolarLoader(worldPath);
						loader.setLoadLighting(!dynamicLighting);
						container.setChunkLoader(loader);
					} catch (IOException e) {
						System.err.println("Runtime error while trying to set chunk loader to polar: " + e.getMessage());
					}
				}
			}
			if (generator != null) {
				Object variables = Variables.copyLocalVariables(event);
				container.setGenerator(unit -> {
					TerrainGenerateEvent generateEvent = new TerrainGenerateEvent(unit);
					Variables.withLocalVariables(variables, generateEvent, () -> TriggerItem.walk(generator, generateEvent));
				});
			}
			if (preloadOption != null) preLoadChunks(container, worldFile, preloadOption.equals("strict"));
		} else {
			assert originalInstance != null; // it won't be null because it's in the pattern
			InstanceContainer instanceContainer = originalInstance.getSingle(event);
			if (instanceContainer == null) return null;
			instance = MinecraftServer.getInstanceManager().createSharedInstance(instanceContainer);
			//instance.enableAutoChunkLoad(instanceContainer.hasEnabledAutoChunkLoad());
		}

		storage.change(event, new Instance[]{instance}, Changer.ChangeMode.SET); // store the created instance on the variable
		return super.walk(event, false);
	}

	public static class TerrainGenerateEvent extends Event {

		private static final HandlerList HANDLERS = new HandlerList();

		private final GenerationUnit unit;

		public TerrainGenerateEvent(GenerationUnit unit) {
			this.unit = unit;
		}

		public GenerationUnit getUnit() {
			return unit;
		}

		@Override
		public HandlerList getHandlers() {
			return HANDLERS;
		}

		public static HandlerList getHandlerList() {
			return HANDLERS;
		}

	}

	private void preLoadChunks(InstanceContainer container, File file, boolean strict) {
		//container.enableAutoChunkLoad(false);
		ChunkLoader loader = container.getChunkLoader();
		boolean loadServerRenderDistance = true;
		if (loader instanceof PolarLoader polarLoader) {
			Collection<PolarChunk> chunks = polarLoader.world().chunks();
			if (!chunks.isEmpty()) loadServerRenderDistance = false;
			for (PolarChunk chunk : chunks) {
				if (container.isChunkLoaded(chunk.x(), chunk.z())) continue; // small optimization
				container.loadChunk(chunk.x(), chunk.z()).whenComplete((c, throwable) -> {
					loadNearbyChunks(container, c);
				});
			}
		} else if (loader instanceof AnvilLoader) {
			if (!phonyAnvilLoader) {
				try {
					int loadedChunks = 0;
					File regionFolder = new File(file, "region");
					if (regionFolder.isDirectory()) {
						File[] mcaFiles = regionFolder.listFiles((dir, name) -> name.endsWith(".mca"));
						if (mcaFiles != null) {
							for (File mcaFile : mcaFiles) {
								String[] parts = mcaFile.getName().split("\\.");
								int regionX = Integer.parseInt(parts[1]);
								int regionZ = Integer.parseInt(parts[2]);
								MiniRegionFile miniRegionFile = new MiniRegionFile(mcaFile);
								for (int localX = 0; localX < 32; localX++) {
									for (int localZ = 0; localZ < 32; localZ++) {
										int chunkX = regionX * 32 + localX;
										int chunkZ = regionZ * 32 + localZ;
										if (!miniRegionFile.hasChunkData(chunkX, chunkZ)) continue;
										loadedChunks++;
										container.loadChunk(chunkX, chunkZ).whenComplete((chunk, throwable) -> {
											loadNearbyChunks(container, chunk);
										});
									}
								}
							}
						}
					}
					loadServerRenderDistance = loadedChunks == 0;
				} catch (Exception e) {
					System.err.println("Runtime error occurred while trying to preload an anvil world: " + e.getMessage());
					return; // don't set chunk loader to no op
				}
			}
		}

		// this will only be called if a provided world file that we're loading from doesn't exist or has no chunks in it
		if (loadServerRenderDistance) {
			int chunkViewDistance = ServerFlag.CHUNK_VIEW_DISTANCE;
			int chunkViewDistancePlus = chunkViewDistance+1;
			for (int x = -chunkViewDistancePlus; x < chunkViewDistancePlus; x++) {
				for (int z = -chunkViewDistancePlus; z < chunkViewDistancePlus; z++) {
					if (container.isChunkLoaded(x, z)) continue;
					loadRenderDistanceChunk(container, x, z, strict, chunkViewDistance);
					loadNearbyChunks(container, x, z,
						(instance, chunkX, chunkZ) -> loadRenderDistanceChunk(instance, chunkX, chunkZ, strict, chunkViewDistance));
				}
			}
		}
		if (strict) {
			//container.enableAutoChunkLoad(false);
			container.setChunkLoader(ChunkLoader.noop());
		}
	}

	private void loadRenderDistanceChunk(Instance container, int x, int z, boolean strict, int chunkViewDistance) {
		if (container.isChunkLoaded(x, z)) return;
		if (strict && (Math.abs(x) > chunkViewDistance || Math.abs(z) > chunkViewDistance)) { // loading outside chunks, shouldn't have extra blocks if we're about to generate
			//container.loadChunk(x, z).join();
			container.generateChunk(x, z, BLANK_GENERATOR);
			return;
		}
		container.loadChunk(x, z);
	}

	/**
	 * Loads chunks around a point in a square fashion, ensuring there's a border of empty chunks around the pasted schematic
	 *
	 * @param instance the {@link Instance} to load the chunks in
	 */
	private void loadNearbyChunks(Instance instance, int originX, int originZ, ChunkLoadOperation operation) {
		for (int x = -1; x < 2; x++) {
			for (int z = -1; z < 2; z++) {
				int newChunkX = originX+x;
				int newChunkZ = originZ+z;
				if (instance.isChunkLoaded(newChunkX, newChunkZ)) continue;
				operation.loadChunk(instance, newChunkX, newChunkZ);
			}
		}
	}

	private void loadNearbyChunks(Instance instance, Chunk originChunk) {
		loadNearbyChunks(instance, originChunk.getChunkX(), originChunk.getChunkZ(), Instance::loadChunk);
	}

	@FunctionalInterface
	interface ChunkLoadOperation {

		void loadChunk(Instance instance, int chunkX, int chunkZ);

	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "todo toString"; // todo finish toString
	}

}
