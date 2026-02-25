package com.github.hapily04.skriptminestom.registration;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.effects.particle.*;
import ch.njol.skript.lang.function.*;
import ch.njol.skript.lang.util.SimpleLiteral;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.DefaultClasses;
import ch.njol.skript.util.Timespan;
import ch.njol.util.coll.CollectionUtils;
import com.github.hapily04.skriptminestom.util.NumberUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.util.RGBLike;
import net.minestom.server.color.AlphaColor;
import net.minestom.server.color.Color;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.item.ItemStack;
import net.minestom.server.particle.Particle;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static com.github.hapily04.skriptminestom.util.MessageUtils.BASIC_MINI_MESSAGE;

@SuppressWarnings("NullableProblems")
public class MinestomFunctions {

	@SuppressWarnings("DataFlowIssue")
	public static void register() {
		Parameter<Number> xParam = new Parameter<>("x", DefaultClasses.NUMBER, true, null);
		Parameter<Number> yParam = new Parameter<>("y", DefaultClasses.NUMBER, true, null);
		Parameter<Number> zParam = new Parameter<>("z", DefaultClasses.NUMBER, true, null);
		Functions.registerFunction(new SimpleJavaFunction<>("position", new Parameter[]{
			xParam,
			yParam,
			zParam,
			new Parameter<>("yaw", DefaultClasses.NUMBER, true, new SimpleLiteral<Number>(0, true)),
			new Parameter<>("pitch", DefaultClasses.NUMBER, true, new SimpleLiteral<Number>(0, true))
		}, Classes.getExactClassInfo(Pos.class), true) {
			@SuppressWarnings("NullableProblems")
			@Override
			public @Nullable Pos @NotNull [] executeSimple(Object[][] params) {
				if (parametersNull(params, 2)) return new Pos[0];
				Number x = (Number) params[0][0];
				Number y = (Number) params[1][0];
				Number z = (Number) params[2][0];
				Number yaw = (Number) params[3][0];
				Number pitch = (Number) params[4][0];
				return new Pos[]{new Pos(x.doubleValue(), y.doubleValue(), z.doubleValue(), yaw.floatValue(), pitch.floatValue())};
			}
		}).description("Creates a position with the given x, y, z, yaw and pitch.").examples("set {_pos} to position(0, 64, 0, 90, 0)");
		Functions.registerFunction(new SimpleJavaFunction<>("vector", new Parameter[]{
			xParam,
			yParam,
			zParam
		}, Classes.getExactClassInfo(Vec.class), true) {
			@Override
			public @Nullable Vec @NotNull [] executeSimple(Object[][] params) {
				if (parametersNull(params, 2)) return new Vec[0];
				Number x = (Number) params[0][0];
				Number y = (Number) params[1][0];
				Number z = (Number) params[2][0];
				return new Vec[]{new Vec(x.doubleValue(), y.doubleValue(), z.doubleValue())};
			}
		}).description("Creates a vector with the given x, y and z.").examples("set {_vec} to vector(1, 0, 0)");
		Functions.registerFunction(new JavaFunction<>("mm", new Parameter[]{
			new Parameter<>("input", DefaultClasses.STRING, true, null),
			new Parameter<>("resolvers", Classes.getExactClassInfo(TagResolver.class), false, new SimpleLiteral<>(new TagResolver[0], TagResolver.class, true))
		}, Classes.getExactClassInfo(Component.class), true) {
			@Override
			public @Nullable Component[] execute(FunctionEvent<?> e, Object[][] params) {
				if (parametersNull(params, 0)) return new Component[0];
				String input = (String) params[0][0];
				TagResolver[] resolvers = (TagResolver[]) params[1];
				return new Component[]{BASIC_MINI_MESSAGE.deserialize(input, resolvers)};
			}
		}).description("Deserializes a MiniMessage string into a Component, with optional tag resolvers.").examples("send mm(\"<red>Hello <name>!\", resolver(\"name\", player's name))");
		Functions.registerFunction(new JavaFunction<>("suggestionEntry", new Parameter[]{
			new Parameter<>("entry", DefaultClasses.STRING, true, null),
			new Parameter<>("tooltip", Classes.getExactClassInfo(Component.class), true, new SimpleLiteral<>(new Component[0], Component.class, true))
		}, Classes.getExactClassInfo(SuggestionEntry.class), true) {
			@Override
			public @Nullable SuggestionEntry[] execute(FunctionEvent<?> e, Object[][] params) {
				if (parametersNull(params, 0)) return new SuggestionEntry[0];
				String entry = (String) params[0][0];
				Component tooltip = (Component) params[1][0];
				return new SuggestionEntry[]{new SuggestionEntry(entry, tooltip)};
			}
		}).description("Deserializes a MiniMessage string into a Component, with optional tag resolvers.").examples("send mm(\"<red>Hello <name>!\", resolver(\"name\", player's name))");
		/*Functions.registerFunction(new SimpleJavaFunction<TagResolver>("tagresolver", new Parameter<>[] {

		}) {
		});*/
		Functions.registerFunction(new SimpleJavaFunction<>("rgb", new Parameter[]{
					 new Parameter<>("red", DefaultClasses.LONG, true, null),
					 new Parameter<>("green", DefaultClasses.LONG, true, null),
					 new Parameter<>("blue", DefaultClasses.LONG, true, null),
					 new Parameter<>("alpha", DefaultClasses.LONG, true, new SimpleLiteral<>(255L, true))
				 }, Classes.getExactClassInfo(Color.class), true) {
					 @Override
					 public Color[] executeSimple(Object[][] params) {
						 if (parametersNull(params, 2)) return new Color[0];
						 Long red = (Long) params[0][0];
						 Long green = (Long) params[1][0];
						 Long blue = (Long) params[2][0];
						 Long alpha = (Long) params[3][0];
						 return CollectionUtils.array(new AlphaColor(alpha.intValue(), red.intValue(), green.intValue(), blue.intValue()));
					 }
				 }).description("Returns a RGB color from the given red, green and blue parameters. Alpha values can be added optionally, " +
					 "but these only take affect in certain situations, like text display backgrounds.")
				 .examples(
					 "dye player's leggings rgb(120, 30, 45)",
					 "set the colour of a text display to rgb(10, 50, 100, 50)"
				 )
				 .since("2.5, 2.10 (alpha)");

		// Particle Data
		Functions.registerFunction(new SimpleJavaFunction<>("dustOption", new Parameter[]{
			new Parameter<>("color", Classes.getExactClassInfo(RGBLike.class), true, null),
			new Parameter<>("size", DefaultClasses.NUMBER, true, new SimpleLiteral<Number>(1f, true))
		}, Classes.getExactClassInfo(DustOption.class), true) {
			@Override
			public DustOption[] executeSimple(Object[][] params) {
				if (parametersNull(params, 0)) return new DustOption[0];
				RGBLike color = (RGBLike) params[0][0];
				Number size = (Number) params[1][0];
				return CollectionUtils.array(new DustOption(color, size.floatValue()));
			}
		}).description("Creates dust options with the given color and size.").examples("set {_data} to dustOption(red, 1)");
		Functions.registerFunction(new SimpleJavaFunction<>("dustTransition", new Parameter[]{
			new Parameter<>("color", Classes.getExactClassInfo(RGBLike.class), true, null),
			new Parameter<>("transition-color", Classes.getExactClassInfo(RGBLike.class), true, null),
			new Parameter<>("size", DefaultClasses.NUMBER, true, new SimpleLiteral<Number>(1f, true))
		}, Classes.getExactClassInfo(DustTransition.class), true) {
			@Override
			public DustTransition[] executeSimple(Object[][] params) {
				if (parametersNull(params, 1)) return new DustTransition[0];
				RGBLike color = (RGBLike) params[0][0];
				RGBLike transitionColor = (RGBLike) params[1][0];
				Number size = (Number) params[2][0];
				return CollectionUtils.array(new DustTransition(color, transitionColor, size.floatValue()));
			}
		}).description("Creates a dust transition with the given color, transition color and size.").examples("set {_data} to dustTransition(red, blue, 1)");
		Functions.registerFunction(new SimpleJavaFunction<>("entityVibrationData", new Parameter[]{
			new Parameter<>("entity", Classes.getExactClassInfo(Entity.class), true, null),
			new Parameter<>("travel-time", Classes.getExactClassInfo(Timespan.class), true, null)
		}, Classes.getExactClassInfo(VibrationData.class), true) {
			@Override
			public VibrationData[] executeSimple(Object[][] params) {
				if (parametersNull(params, 1)) return new VibrationData[0];
				Entity entity = (Entity) params[0][0];
				Timespan travelTime = (Timespan) params[1][0];
				return CollectionUtils.array(new VibrationData(Particle.Vibration.SourceType.ENTITY, null,
					entity.getEntityId(), (float) entity.getEyeHeight(), (int) NumberUtils.ticksFrom(travelTime)));
			}
		}).description("Creates vibration data targeting an entity.").examples("set {_data} to entityVibrationData(player, 5 seconds)");
		Functions.registerFunction(new SimpleJavaFunction<>("blockVibrationData", new Parameter[]{
			new Parameter<>("block", Classes.getExactClassInfo(Point.class), true, null),
			new Parameter<>("travel-time", Classes.getExactClassInfo(Timespan.class), true, null)
		}, Classes.getExactClassInfo(VibrationData.class), true) {
			@Override
			public VibrationData[] executeSimple(Object[][] params) {
				if (parametersNull(params, 1)) return new VibrationData[0];
				Point point = (Point) params[0][0];
				Timespan travelTime = (Timespan) params[1][0];
				return CollectionUtils.array(new VibrationData(Particle.Vibration.SourceType.BLOCK, point,
					-1, 0, (int) NumberUtils.ticksFrom(travelTime)));
			}
		}).description("Creates vibration data targeting a block.").examples("set {_data} to blockVibrationData(point(0, 64, 0), 5 seconds)");
		Functions.registerFunction(new SimpleJavaFunction<>("trailData", new Parameter[]{
			new Parameter<>("target", Classes.getExactClassInfo(Point.class), true, null),
			new Parameter<>("color", Classes.getExactClassInfo(RGBLike.class), true, null),
			new Parameter<>("duration", Classes.getExactClassInfo(Timespan.class), true, null)
		}, Classes.getExactClassInfo(TrailData.class), true) {
			@Override
			public TrailData[] executeSimple(Object[][] params) {
				if (parametersNull(params, 2)) return new TrailData[0];
				Point target = (Point) params[0][0];
				RGBLike color = (RGBLike) params[1][0];
				Timespan duration = (Timespan) params[2][0];
				return CollectionUtils.array(new TrailData(target, color, (int) NumberUtils.ticksFrom(duration)));
			}
		}).description("Creates trail data for a trial particle.").examples("set {_data} to trailData(point(0, 64, 0), red, 5 seconds)");
		Functions.registerFunction(new SimpleJavaFunction<>("effectData", new Parameter[]{
			new Parameter<>("color", Classes.getExactClassInfo(RGBLike.class), true, null),
			new Parameter<>("power", Classes.getExactClassInfo(Number.class), true, null)
		}, Classes.getExactClassInfo(EffectData.class), true) {
			@Override
			public EffectData[] executeSimple(Object[][] params) {
				if (parametersNull(params, 1)) return new EffectData[0];
				RGBLike color = (RGBLike) params[0][0];
				Number power = (Number) params[1][0];
				return CollectionUtils.array(new EffectData(color, power.floatValue()));
			}
		}).description("Creates effect data for an effect particle.").examples("set {_data} to effectData(red, 1)");
		Functions.registerFunction(new SimpleJavaFunction<>("resolver", new Parameter[]{
			new Parameter<>("name", Classes.getExactClassInfo(String.class), true, null),
			new Parameter<>("value", Classes.getExactClassInfo(Object.class), true, null),
			new Parameter<>("parsed", Classes.getExactClassInfo(Boolean.class), true, new SimpleLiteral<>(false, true))
		}, Classes.getExactClassInfo(TagResolver.class), true) {
			@Override
			public TagResolver[] executeSimple(Object[][] params) {
				if (parametersNull(params, 1)) return new TagResolver[0];
				String name = (String) params[0][0];
				Object value = params[1][0];
				boolean parsed = (boolean) params[2][0];
				if (value instanceof String s) return CollectionUtils.array(parsed ? Placeholder.parsed(name, s) : Placeholder.unparsed(name, s));
				if (value instanceof ComponentLike c) return CollectionUtils.array(Placeholder.component(name, c));
				return new TagResolver[0];
			}
		}).description("Creates a MiniMessage tag resolver.").examples("set {_resolver} to resolver(\"name\", player's name)");
	}

	private static boolean parametersNull(Object[][] params, int toIndex) {
		for (int i = 0; i <= toIndex; i++) {
			if (params[i].length == 0) return true;
		}
		return false;
	}

}
