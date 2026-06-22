package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.kyori.adventure.util.RGBLike;
import net.minestom.server.color.AlphaColor;
import net.minestom.server.utils.MathUtils;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;


@Name("Color With Alpha")
@Description("A color with a specific opacity/alpha value.")
@Examples("set {_c} to red with alpha value 128")
public class ExprColorWithAlpha extends SimpleExpression<AlphaColor> {

	static {
		Skript.registerExpression(ExprColorWithAlpha.class, AlphaColor.class, ExpressionType.COMBINED,
			"%rgblikes% with (opacity|alpha) [value] %integer%");
	}

	private Expression<RGBLike> color;
	private Expression<Integer> alpha;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		color = (Expression<RGBLike>) expressions[0];
		alpha = (Expression<Integer>) expressions[1];
		if (alpha instanceof Literal<Integer> literal && !MathUtils.isBetween(literal.getSingle(), 0, 255)) {
			Skript.error("Alpha needs to be between 0 and 255.");
			return false;
		}
		return true;
	}

	@Override
	protected AlphaColor @Nullable [] get(Event event) {
		Integer alpha = this.alpha.getSingle(event);
		if (alpha == null) return null;
		RGBLike[] colors = color.getArray(event);
		AlphaColor[] alphaColors = new AlphaColor[colors.length];
		for (int i = 0; i < colors.length; i++) {
			alphaColors[i] = new AlphaColor(alpha, colors[i]);
		}
		return alphaColors;
	}

	@Override
	public boolean isSingle() {
		return color.isSingle();
	}

	@Override
	public Class<? extends AlphaColor> getReturnType() {
		return AlphaColor.class;
	}

	@Override
	public String toString(@org.eclipse.jdt.annotation.Nullable Event event, boolean debug) {
		return color.toString(event, debug) + " with alpha " + alpha.toString(event, debug);
	}

}
