package ch.njol.skript.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.util.SimpleLiteral;
import ch.njol.skript.log.SkriptLogger;
import ch.njol.util.Kleenean;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.util.RGBLike;
import net.minestom.server.MinecraftServer;
import net.minestom.server.color.Color;
import net.minestom.server.registry.DynamicRegistry;
import net.minestom.server.world.DimensionType;
import net.minestom.server.world.attribute.AmbientParticle;
import net.minestom.server.world.attribute.EnvironmentAttribute;
import net.minestom.server.world.biome.Biome;
import net.minestom.server.world.biome.BiomeEffects;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.util.ExpressionEntryData;
import org.skriptlang.skript.lang.entry.util.LiteralEntryData;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

@SuppressWarnings({"rawtypes", "unchecked"})
public class EffSecCreateEnvironment extends EffectSection {

	private static final EntryValidator DIMENSION_ENTRY_VALIDATOR;
	private static final EntryValidator BIOME_ENTRY_VALIDATOR;
	private static final List<EnvironmentAttributeWrapper> ENVIRONMENT_ATTRIBUTES = new ArrayList<>();

	static {
		EntryValidator.EntryValidatorBuilder dimensionBuilder = EntryValidator.builder();
		dimensionBuilder
			.addEntryData(new LiteralEntryData<>("fixed time", false, true, Boolean.class))
			.addEntryData(new LiteralEntryData<>("sky light", null, true, Boolean.class))
			.addEntryData(new LiteralEntryData<>("ceiling", null, true, Boolean.class))
			.addEntryData(new LiteralEntryData<>("coordinate scale", null, true, Number.class))
			.addEntryData(new LiteralEntryData<>("minimum y", null, true, Integer.class))
			.addEntryData(new LiteralEntryData<>("maximum y", null, true, Integer.class))
			.addEntryData(new LiteralEntryData<>("logical y", null, true, Integer.class))
			.addEntryData(new LiteralEntryData<>("infiniburn", null, true, String.class))
			.addEntryData(new LiteralEntryData<>("ambient light", null, true, Number.class))
			.addEntryData(new LiteralEntryData<>("skybox", null, true, DimensionType.Skybox.class))
			.addEntryData(new LiteralEntryData<>("cardinal light", null, true, DimensionType.CardinalLight.class));
			// todo timelines

		EntryValidator.EntryValidatorBuilder biomeBuilder = EntryValidator.builder();
		biomeBuilder
			.addEntryData(new LiteralEntryData<>("precipitation", true, true, Boolean.class))
			.addEntryData(new LiteralEntryData<>("temperature", 0.8f, true, Number.class))
			.addEntryData(new LiteralEntryData<>("temperature modifier", Biome.TemperatureModifier.NONE, true, Biome.TemperatureModifier.class))
			.addEntryData(new LiteralEntryData<>("downfall", 0.4f, true, Number.class))
			.addEntryData(new LiteralEntryData<>("water color", new Color(0x3f76e4), true, RGBLike.class))
			.addEntryData(new LiteralEntryData<>("foliage color", null, true, RGBLike.class))
			.addEntryData(new LiteralEntryData<>("dry foliage color", null, true, RGBLike.class))
			.addEntryData(new LiteralEntryData<>("grass color", null, true, RGBLike.class))
			.addEntryData(new LiteralEntryData<>("grass color modifier", BiomeEffects.GrassColorModifier.NONE, true, BiomeEffects.GrassColorModifier.class));

		for (EnvironmentAttribute<?> value : EnvironmentAttribute.values()) {
			String key = value.key().asMinimalString().split("/")[1].replace('_', ' ');
			Object defaultValue = value.defaultValue();
			Class<?> type = defaultValue.getClass();
			if (defaultValue instanceof List<?>) type = AmbientParticle.class;
			if (Number.class.isAssignableFrom(type)) type = Number.class;
			// noinspection unchecked
			ExpressionEntryData<Object> entryData = new ExpressionEntryData<>(key, new SimpleLiteral<>(defaultValue, true), true, (Class<Object>) type);
			dimensionBuilder.addEntryData(entryData);
			biomeBuilder.addEntryData(entryData);
			ENVIRONMENT_ATTRIBUTES.add(new EnvironmentAttributeWrapper(key, value, type));
		}
		DIMENSION_ENTRY_VALIDATOR = dimensionBuilder.build();
		BIOME_ENTRY_VALIDATOR = biomeBuilder.build();

		Skript.registerSection(EffSecCreateEnvironment.class,
			"create (:dimension|biome) [type] under [name[ ]space] %string% [(and store it|stored) in %-objects%]");
	}

	private EntryContainer container;
	private Expression<String> namespace;
	@Nullable
	private Expression<Object> storage;
	private boolean dimension;

	private Boolean fixedTime;
	private Boolean skyLight;
	private Boolean ceiling;
	private Number coordinateScale;
	private Integer minY;
	private Integer maxY;
	private Integer logicalY;
	private String infiniburn;
	private Number ambientLight;
	private DimensionType.Skybox skybox;
	private DimensionType.CardinalLight cardinalLight;

	private Boolean precipitaion;
	private Number temperature;
	private Biome.TemperatureModifier temperatureModifier;
	private Number downfall;
	private RGBLike waterColor;
	private RGBLike foliageColor;
	private RGBLike dryFoliageColor;
	private RGBLike grassColor;
	private BiomeEffects.GrassColorModifier grassColorModifier;

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult,
						@Nullable SectionNode sectionNode, @Nullable List<TriggerItem> triggerItems) {
		dimension = parseResult.hasTag("dimension");
		if (!isDelayed.isFalse()) {
			Skript.error("A " + getEnvName() + " creation section cannot be delayed.");
			return false;
		}

		namespace = (Expression<String>) expressions[0];
		storage = (Expression<Object>) expressions[1];

		if (namespace instanceof Literal<String> lit) {
			String id = lit.getSingle();
			if (!Key.parseable(id)) {
				Skript.error("Provided namespace isn't parseable. Format is 'prefix:value'.");
				return false;
			}
		}

		if (sectionNode != null) {
			if (dimension) {
				container = DIMENSION_ENTRY_VALIDATOR.validate(sectionNode);
				if (container == null) return false;

				fixedTime = container.getOptional("fixed time", Boolean.class, true);
				skyLight = container.getOptional("sky light", Boolean.class, true);
				ceiling = container.getOptional("ceiling", Boolean.class, true);
				coordinateScale = container.getOptional("coordinate scale", Number.class, true);
				minY = container.getOptional("minimum y", Integer.class, true);
				maxY = container.getOptional("maximum y", Integer.class, true);
				logicalY = container.getOptional("logical y", Integer.class, true);
				infiniburn = container.getOptional("infiniburn", String.class, true);
				ambientLight = container.getOptional("ambient light", Number.class, true);
				skybox = container.getOptional("skybox", DimensionType.Skybox.class, true);
				cardinalLight = container.getOptional("cardinal light", DimensionType.CardinalLight.class, true);
			} else {
				container = BIOME_ENTRY_VALIDATOR.validate(sectionNode);
				if (container == null) return false;

				precipitaion = container.getOptional("precipitation", Boolean.class, true);
				temperature = container.getOptional("temperature", Number.class, true);
				temperatureModifier = container.getOptional("temperature modifier", Biome.TemperatureModifier.class, true);
				downfall = container.getOptional("downfall", Number.class, true);
				waterColor = container.getOptional("water color", RGBLike.class, true);
				foliageColor = container.getOptional("foliage color", RGBLike.class, true);
				dryFoliageColor = container.getOptional("dry foliage color", RGBLike.class, true);
				grassColor = container.getOptional("grass color", RGBLike.class, true);
				grassColorModifier = container.getOptional("grass color modifier", BiomeEffects.GrassColorModifier.class, true);
			}
		}
		return true;
	}

	@Override
	protected @Nullable TriggerItem walk(Event event) {
		String namespace = this.namespace.getSingle(event);
		if (!Key.parseable(namespace)) {
			SkriptLogger.LOGGER.error("Couldn't register {} under '{}' because the namespace is in the wrong format.", getEnvName(), namespace);
			return super.walk(event, false);
		}
		Key key = Key.key(namespace);
		DynamicRegistry<?> registry = dimension ? MinecraftServer.getDimensionTypeRegistry() : MinecraftServer.getBiomeRegistry();
		if (registry.get(key) != null) {
			SkriptLogger.LOGGER.error("{} is already registered under '{}'.", getEnvName(), namespace);
			return super.walk(event, false);
		}

		Object[] storageValue;
		if (dimension) {
			DimensionType.Builder builder = DimensionType.builder();
			builder.timelines(((DimensionType) registry.get(DimensionType.OVERWORLD.key())).timelines());
			if (container != null) {
				if (fixedTime != null) builder.fixedTime(fixedTime);
				if (skyLight != null) builder.skylight(skyLight);
				if (ceiling != null) builder.ceiling(ceiling);
				if (coordinateScale != null) builder.coordinateScale(coordinateScale.doubleValue());
				if (minY != null) builder.minY(minY);
				if (maxY != null) builder.height(maxY);
				if (logicalY != null) builder.logicalHeight(logicalY);
				if (infiniburn != null) builder.infiniburn(infiniburn);
				if (ambientLight != null) builder.ambientLight(ambientLight.floatValue());
				if (skybox != null) builder.skybox(skybox);
				if (cardinalLight != null) builder.cardinalLight(cardinalLight);
				applyAttributes((attribute, o) -> setAttribute(builder, attribute, o), event);
			}

			DimensionType dimensionType = builder.build();
			storageValue = new DimensionType[]{dimensionType};
			register(key, registry, dimensionType);
		} else {
			Biome.Builder builder = Biome.builder();
			if (container != null) {
				builder
					.precipitation(precipitaion)
					.temperature(temperature.floatValue())
					.temperatureModifier(temperatureModifier)
					.downfall(downfall.floatValue())
					.effects(BiomeEffects.builder()
						.waterColor(waterColor)
						.foliageColor(foliageColor)
						.dryFoliageColor(dryFoliageColor)
						.grassColor(grassColor)
						.grassColorModifier(grassColorModifier)
						.build());
				applyAttributes((attribute, o) -> setAttribute(builder, attribute, o), event);
			}

			Biome biome = builder.build();
			storageValue = new Biome[]{biome};
			register(key, registry, biome);
		}

		if (storage != null) storage.change(event, storageValue, Changer.ChangeMode.SET); // store the created environment on the variable
		return super.walk(event, false);
	}

	public void applyAttributes(BiConsumer<EnvironmentAttribute<?>, Object> consumer, Event event) {
		for (EnvironmentAttributeWrapper wrapper : ENVIRONMENT_ATTRIBUTES) {
			Expression<?> value = container.getOptional(wrapper.key, Expression.class, false);
			if (value == null) continue;
			Object val;
			if (value.isSingle()) {
				val = value.getSingle(event);
				if (val == null) continue;
				if (val instanceof Number number) val = number.floatValue();
				if (val instanceof AmbientParticle ambientParticle) val = List.of(ambientParticle);
			} else {
				Object[] obj = value.getArray(event);
				val = List.of(obj);
			}
			consumer.accept(wrapper.value, val);
		}
	}

	public void register(Key key, DynamicRegistry registry, Object o) {
		registry.register(key, o);
	}

	private void setAttribute(DimensionType.Builder builder, EnvironmentAttribute attribute, Object value) {
		builder.setAttribute(attribute, value);
	}

	private void setAttribute(Biome.Builder builder, EnvironmentAttribute attribute, Object value) {
		builder.setAttribute(attribute, value);
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "create " + getEnvName() + " under " + namespace.toString(event, debug) + " stored in " + storage.toString(event, debug);
	}

	private String getEnvName() {
		return dimension ? "dimension" : "biome";
	}

	private record EnvironmentAttributeWrapper(String key, EnvironmentAttribute<?> value, Class<?> type) { }

}
