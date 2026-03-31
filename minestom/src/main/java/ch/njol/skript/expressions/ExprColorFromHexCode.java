package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.simplification.SimplifiedLiteral;
import net.minestom.server.color.AlphaColor;
import net.minestom.server.color.Color;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

@Name("Color from Hex Code")
@Description("Returns a proper argb color from a hex code string. The hex code must contain RRGGBB values, but can also " +
	"contain a leading # or AARRGGBB format. Invalid codes will cause runtime errors.")
@Example("send color from hex code \"#FFBBA7\"")
@Example("send color from hex code \"FFBBA7\"")
@Example("send color from hex code \"#AAFFBBA7\"")
@Since("2.14")
public class ExprColorFromHexCode extends SimplePropertyExpression<String, AlphaColor> {

	// https://stackoverflow.com/a/13667522
	private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("^[0-9a-fA-F]{6}([0-9a-fA-F]{2})?$");

	static {
		Skript.registerExpression(ExprColorFromHexCode.class, AlphaColor.class, ExpressionType.PROPERTY,
			"[the] colo[u]r[s] (from|of) hex[adecimal] code[s] %strings%");
	}

	@Override
	public @Nullable AlphaColor convert(String from) {
		if (from.startsWith("#")) // strip leading #
			from = from.substring(1);
		if (!HEX_COLOR_PATTERN.matcher(from).matches()) return null;
		return new AlphaColor(Integer.decode(from));
	}

	@Override
	public Class<? extends AlphaColor> getReturnType() {
		return AlphaColor.class;
	}

	@Override
	protected String getPropertyName() {
		return "ExprColorFromHexCode - UNUSED";
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "the color of hex code " + getExpr().toString(event, debug);
	}

	@Override
	public Expression<? extends AlphaColor> simplify() {
		if (getExpr() instanceof Literal<?>) {
			return SimplifiedLiteral.fromExpression(this);
		}
		return this;
	}

}
