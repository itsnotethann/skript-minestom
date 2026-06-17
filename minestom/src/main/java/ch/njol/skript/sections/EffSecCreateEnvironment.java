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

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@SuppressWarnings({"rawtypes", "unchecked"})
public class EffSecCreateEnvironment extends EffectSection {

	private static final EntryValidator DIMENSION_ENTRY_VALIDATOR;
	private static final EntryValidator BIOME_ENTRY_VALIDATOR;
	private static final List<EnvironmentAttributeWrapper> ENVIRONMENT_ATTRIBUTES = new ArrayList<>();

	static {
		EntryValidator.EntryValidatorBuilder dimensionBuilder = EntryValidator.builder();
		dimensionBuilder
			.addEntryData(new ExpressionEntryData<>("fixed time", new SimpleLiteral<>(false, true), true, Boolean.class))
			.addEntryData(new ExpressionEntryData<>("sky light", null, true, Boolean.class))
			.addEntryData(new ExpressionEntryData<>("ceiling", null, true, Boolean.class))
			.addEntryData(new ExpressionEntryData<>("coordinate scale", null, true, Number.class))
			.addEntryData(new ExpressionEntryData<>("minimum y", null, true, Integer.class))
			.addEntryData(new ExpressionEntryData<>("maximum y", null, true, Integer.class))
			.addEntryData(new ExpressionEntryData<>("logical y", null, true, Integer.class))
			.addEntryData(new ExpressionEntryData<>("infiniburn", null, true, String.class))
			.addEntryData(new ExpressionEntryData<>("ambient light", null, true, Number.class))
			.addEntryData(new ExpressionEntryData<>("skybox", null, true, DimensionType.Skybox.class))
			.addEntryData(new ExpressionEntryData<>("cardinal light", null, true, DimensionType.CardinalLight.class));
			// todo timelines

		EntryValidator.EntryValidatorBuilder biomeBuilder = EntryValidator.builder();
		biomeBuilder
			.addEntryData(new ExpressionEntryData<>("precipitation", new SimpleLiteral<>(true, true), true, Boolean.class))
			.addEntryData(new ExpressionEntryData<>("temperature", new SimpleLiteral<>(0.8f, true), true, Number.class))
			.addEntryData(new ExpressionEntryData<>("temperature modifier", new SimpleLiteral<>(Biome.TemperatureModifier.NONE, true), true, Biome.TemperatureModifier.class))
			.addEntryData(new ExpressionEntryData<>("downfall", new SimpleLiteral<>(0.4f, true), true, Number.class))
			.addEntryData(new ExpressionEntryData<>("water color", new SimpleLiteral<>(new Color(0x3f76e4), true), true, RGBLike.class))
			.addEntryData(new ExpressionEntryData<>("foliage color", null, true, RGBLike.class))
			.addEntryData(new ExpressionEntryData<>("dry foliage color", null, true, RGBLike.class))
			.addEntryData(new ExpressionEntryData<>("grass color", null, true, RGBLike.class))
			.addEntryData(new ExpressionEntryData<>("grass color modifier", new SimpleLiteral<>(BiomeEffects.GrassColorModifier.NONE, true), true, BiomeEffects.GrassColorModifier.class));

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

	private Expression<Boolean> fixedTime;
	private Expression<Boolean> skyLight;
	private Expression<Boolean> ceiling;
	private Expression<Number> coordinateScale;
	private Expression<Integer> minY;
	private Expression<Integer> maxY;
	private Expression<Integer> logicalY;
	private Expression<String> infiniburn;
	private Expression<Number> ambientLight;
	private Expression<DimensionType.Skybox> skybox;
	private Expression<DimensionType.CardinalLight> cardinalLight;

	private Expression<Boolean> precipitation;
	private Expression<Number> temperature;
	private Expression<Biome.TemperatureModifier> temperatureModifier;
	private Expression<Number> downfall;
	private Expression<RGBLike> waterColor;
	private Expression<RGBLike> foliageColor;
	private Expression<RGBLike> dryFoliageColor;
	private Expression<RGBLike> grassColor;
	private Expression<BiomeEffects.GrassColorModifier> grassColorModifier;

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

				fixedTime = (Expression<Boolean>) container.getOptional("fixed time", true);
				skyLight = (Expression<Boolean>) container.getOptional("sky light", true);
				ceiling = (Expression<Boolean>) container.getOptional("ceiling", true);
				coordinateScale = (Expression<Number>) container.getOptional("coordinate scale", true);
				minY = (Expression<Integer>) container.getOptional("minimum y", true);
				maxY = (Expression<Integer>) container.getOptional("maximum y", true);
				logicalY = (Expression<Integer>) container.getOptional("logical y", true);
				infiniburn = (Expression<String>) container.getOptional("infiniburn", true);
				ambientLight = (Expression<Number>) container.getOptional("ambient light", true);
				skybox = (Expression<DimensionType.Skybox>) container.getOptional("skybox", true);
				cardinalLight = (Expression<DimensionType.CardinalLight>) container.getOptional("cardinal light", true);
			} else {
				container = BIOME_ENTRY_VALIDATOR.validate(sectionNode);
				if (container == null) return false;

				precipitation = (Expression<Boolean>) container.getOptional("precipitation", true);
				temperature = (Expression<Number>) container.getOptional("temperature", true);
				temperatureModifier = (Expression<Biome.TemperatureModifier>) container.getOptional("temperature modifier", true);
				downfall = (Expression<Number>) container.getOptional("downfall", true);
				waterColor = (Expression<RGBLike>) container.getOptional("water color", false);
				foliageColor = (Expression<RGBLike>) container.getOptional("foliage color", false);
				dryFoliageColor = (Expression<RGBLike>) container.getOptional("dry foliage color", true);
				grassColor = (Expression<RGBLike>) container.getOptional("grass color", true);
				grassColorModifier = (Expression<BiomeEffects.GrassColorModifier>) container.getOptional("grass color modifier", true);
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
				executeIfPresent(fixedTime, event, builder::fixedTime);
				executeIfPresent(skyLight, event, builder::skylight);
				executeIfPresent(ceiling, event, builder::ceiling);
				executeIfPresent(coordinateScale, event, number -> builder.coordinateScale(number.doubleValue()));
				executeIfPresent(minY, event, builder::minY);
				executeIfPresent(maxY, event, builder::height);
				executeIfPresent(logicalY, event, builder::logicalHeight);
				executeIfPresent(infiniburn, event, builder::infiniburn);
				executeIfPresent(ambientLight, event, number -> builder.ambientLight(number.floatValue()));
				executeIfPresent(skybox, event, builder::skybox);
				executeIfPresent(cardinalLight, event, builder::cardinalLight);
				applyAttributes((attribute, o) -> setAttribute(builder, attribute, o), event);
			}

			DimensionType dimensionType = builder.build();
			storageValue = new DimensionType[]{dimensionType};
			register(key, registry, dimensionType);
		} else {
			Biome.Builder builder = Biome.builder();
			if (container != null) {
				BiomeEffects.Builder effectsBuilder = BiomeEffects.builder();
				executeIfPresent(waterColor, event, effectsBuilder::waterColor);
				executeIfPresent(foliageColor, event, effectsBuilder::foliageColor);
				executeIfPresent(dryFoliageColor, event, effectsBuilder::dryFoliageColor);
				executeIfPresent(grassColor, event, effectsBuilder::grassColor);
				executeIfPresent(grassColorModifier, event, effectsBuilder::grassColorModifier);
				BiomeEffects effects = effectsBuilder.build();

				builder.effects(effects);
				executeIfPresent(precipitation, event, builder::precipitation);
				executeIfPresent(temperature, event, number -> builder.temperature(number.floatValue()));
				executeIfPresent(temperatureModifier, event, builder::temperatureModifier);
				executeIfPresent(downfall, event, number -> builder.downfall(number.floatValue()));

				applyAttributes((attribute, o) -> setAttribute(builder, attribute, o), event);
			}

			Biome biome = builder.build();
			storageValue = new Biome[]{biome};
			register(key, registry, biome);
		}

		if (storage != null) storage.change(event, storageValue, Changer.ChangeMode.SET); // store the created environment on the variable
		return super.walk(event, false);
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "create " + getEnvName() + " under " + namespace.toString(event, debug) + (storage != null ? (" stored in " + storage.toString(event, debug)) : "");
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

	private <T> void executeIfPresent(@Nullable Expression<T> expr, Event event, Consumer<T> consumer) {
		if (expr == null) return;
		T value = expr.getSingle(event);
		if (value == null) return;
		consumer.accept(value);
	}

	private String getEnvName() {
		return dimension ? "dimension" : "biome";
	}

	private record EnvironmentAttributeWrapper(String key, EnvironmentAttribute<?> value, Class<?> type) { }

}
