package com.github.hapily04.skriptminestom.registration;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.lang.function.*;
import ch.njol.skript.lang.util.SimpleLiteral;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.DefaultClasses;
import ch.njol.util.coll.CollectionUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.minestom.server.color.AlphaColor;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.item.ItemStack;
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
				Number x = (Number) params[0][0];
				Number y = (Number) params[1][0];
				Number z = (Number) params[2][0];
				Number yaw = (Number) params[3][0];
				Number pitch = (Number) params[4][0];
				return new Pos[]{new Pos(x.doubleValue(), y.doubleValue(), z.doubleValue(), yaw.floatValue(), pitch.floatValue())};
			}
		});
		Functions.registerFunction(new SimpleJavaFunction<>("vector", new Parameter[]{
			xParam,
			yParam,
			zParam
		}, Classes.getExactClassInfo(Vec.class), true) {
			@Override
			public @Nullable Vec @NotNull [] executeSimple(Object[][] params) {
				Number x = (Number) params[0][0];
				Number y = (Number) params[1][0];
				Number z = (Number) params[2][0];
				return new Vec[]{new Vec(x.doubleValue(), y.doubleValue(), z.doubleValue())};
			}
		});
		Functions.registerFunction(new JavaFunction<>("mm", new Parameter[]{
			new Parameter<>("input", DefaultClasses.STRING, true, null),
			new Parameter<>("resolvers", Classes.getExactClassInfo(TagResolver.class), false, new SimpleLiteral<>(new TagResolver[0], TagResolver.class, true))
		}, Classes.getExactClassInfo(Component.class), true) {
			@Override
			public @Nullable Component[] execute(FunctionEvent<?> e, Object[][] params) {
				String input = (String) params[0][0];
				TagResolver[] resolvers = (TagResolver[]) params[1];
				return new Component[]{BASIC_MINI_MESSAGE.deserialize(input, resolvers)};
			}
		});
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
						 Long red = (Long) params[0][0];
						 Long green = (Long) params[1][0];
						 Long blue = (Long) params[2][0];
						 Long alpha = (Long) params[3][0];

						 return CollectionUtils.array(new AlphaColor(red.intValue(), green.intValue(), blue.intValue(), alpha.intValue()));
					 }
				 }).description("Returns a RGB color from the given red, green and blue parameters. Alpha values can be added optionally, " +
					 "but these only take affect in certain situations, like text display backgrounds.")
				 .examples(
					 "dye player's leggings rgb(120, 30, 45)",
					 "set the colour of a text display to rgb(10, 50, 100, 50)"
				 )
				 .since("2.5, 2.10 (alpha)");
	}

}
