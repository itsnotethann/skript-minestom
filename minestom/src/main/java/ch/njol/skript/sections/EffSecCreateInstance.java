package ch.njol.skript.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.*;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.github.hapily04.skriptminestom.util.FileUtils;
import com.github.hapily04.skriptminestom.util.MiniRegionFile;
import net.hollowcube.polar.PolarChunk;
import net.hollowcube.polar.PolarLoader;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.ChunkLoader;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.registry.RegistryKey;
import net.minestom.server.world.DimensionType;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.eclipse.jdt.annotation.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class EffSecCreateInstance extends EffectSection {

	private static final EntryValidator ENTRY_VALIDATOR;
	private static final List<String> VALID_LOADER_ENTRIES = List.of("anvil", "polar");
	// maybe a preload-super-strict option that only loads chunks w blocks in them instead of every chunk that the world provides?
	private static final List<String> VALID_GENERATOR_PRESET_ENTRIES = List.of("preload-strict", "preload");

	static {
		ENTRY_VALIDATOR = EntryValidator.builder()
										.addEntry("file", null, true)
										.addEntry("loader", null, true)
										//.addEntryData(new ExpressionEntryData<>("dimension", null, true, DimensionType.class))
										.addSection("generator", true)
										.addEntry("generator preset", null, true)
										.build();
		Skript.registerSection(EffSecCreateInstance.class,
			"create instance [container] (and store it|stored) in %objects%",
			"create shared instance from [instance] %instancecontainer% (and store it|stored) in %objects%");
	}

	private int matchedPattern;
	@SuppressWarnings("NotNullFieldNotInitialized")
	private Expression<Object> storage;
	@Nullable
	private Expression<InstanceContainer> originalInstance;
	@Nullable
	private File worldFile;
	@Nullable
	private String loader;
	@Nullable
	private Expression<DimensionType> dimension;
	@Nullable
	private String generatorPreset;
	@Nullable
	private Trigger generator;
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

			String generatorPreset = container.getOptional("generator preset", String.class, false);
			if (generatorPreset != null) {
				if (!VALID_GENERATOR_PRESET_ENTRIES.contains(generatorPreset)) {
					Skript.error("An invalid generator preset has been provided: '" + generatorPreset + "'.");
					return false;
				}
				if (generatorPreset.equals("preload") && worldFile == null) {
					Skript.error("Generator preset 'preload' was selected, but no world file was provided to preload.");
					return false;
				}
				this.generatorPreset = generatorPreset;
			}

			SectionNode generator = container.getOptional("generator", SectionNode.class, false);
			if (generator != null) {
				if (generatorPreset != null) {
					Skript.error("You cannot have a generator if a generator preset has been set.");
					return false;
				}
				this.generator = loadCode(generator, "generator", TerrainGenerateEvent.class);
			}
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
			} else instance = MinecraftServer.getInstanceManager().createInstanceContainer();
		}
		else {
			assert originalInstance != null; // it won't be null because it's in the pattern
			InstanceContainer instanceContainer = originalInstance.getSingle(event);
			if (instanceContainer == null) return null;
			instance = MinecraftServer.getInstanceManager().createSharedInstance(instanceContainer);
		}
		if (matchedPattern == 0) {
			assert instance instanceof InstanceContainer;
			InstanceContainer container = (InstanceContainer) instance;
			assert worldFile != null; // shouldn't be null because we throw a skript error if the file doesn't exist or if it's null
			if (loader != null) {
				Path worldPath = worldFile.toPath();
				if (loader.equalsIgnoreCase("anvil")) {
					if (!worldFile.isDirectory() || !new File(worldFile, "region").isDirectory()) {
						MinecraftServer.getInstanceManager().unregisterInstance(container);
						return null;
					}
					container.setChunkLoader(new AnvilLoader(worldFile.toPath()));
				} else {
					try {
						container.setChunkLoader(new PolarLoader(worldPath));
					} catch (IOException e) {
						System.err.println("Runtime error while trying to set chunk loader to polar: " + e.getMessage());
					}
				}
			}
			if (generatorPreset != null && generatorPreset.contains("preload")) preLoadChunks(container, worldFile, generatorPreset.contains("strict"));
			else if (generator != null) {
				Object variables = Variables.copyLocalVariables(event);
				container.setGenerator(unit -> {
					TerrainGenerateEvent generateEvent = new TerrainGenerateEvent(unit);
					Variables.withLocalVariables(variables, generateEvent, () -> TriggerItem.walk(generator, generateEvent));
				});
			}
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

	// todo chunk preloader for anvil and polar
	private void preLoadChunks(InstanceContainer container, File file, boolean strict) {
		container.enableAutoChunkLoad(false);
		ChunkLoader loader = container.getChunkLoader();
		if (loader instanceof PolarLoader polarLoader) {
			for (PolarChunk chunk : polarLoader.world().chunks()) {
				container.loadChunk(chunk.x(), chunk.z()).whenComplete((c, throwable) -> {
					loadNearByChunks(container, c);
				});
			}
		} else if (loader instanceof AnvilLoader) {
			try {
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
									container.loadChunk(chunkX, chunkZ).whenComplete((chunk, throwable) -> {
										loadNearByChunks(container, chunk);
									});
								}
							}
						}
					}
				}
			} catch (Exception e) {
				System.err.println("Runtime error occurred while trying to preload an anvil world: " + e.getMessage());
				return; // don't set chunk loader to no op
			}
		}
		if (strict) container.setChunkLoader(ChunkLoader.noop());
	}

	/**
	 * Loads chunks around a point in a square fashion, ensuring there's a border of empty chunks around the pasted schematic
	 *
	 * @param instance the {@link Instance} to load the chunks in
	 * @param originChunk the original chunk to load the chunks around
	 */
	private void loadNearByChunks(Instance instance, Chunk originChunk) {
		if (originChunk == null) return;
		int originChunkX = originChunk.getChunkX();
		int originChunkZ = originChunk.getChunkZ();
		for (int x = -1; x < 2; x++) {
			for (int z = -1; z < 2; z++) {
				int newChunkX = originChunkX+x;
				int newChunkZ = originChunkZ+z;
				if (instance.isChunkLoaded(newChunkX, newChunkZ)) continue;
				instance.loadChunk(newChunkX, newChunkZ);
			}
		}
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "todo toString"; // todo finish toString
	}

}
