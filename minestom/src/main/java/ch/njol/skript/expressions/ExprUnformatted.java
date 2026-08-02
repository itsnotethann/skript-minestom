package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.ComponentWrapper;
import ch.njol.util.Kleenean;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Unformatted")
@Description("Strip coloring, hover events, etc. and make the provided component a plain string.")
@Example("send unformatted {_component}")
public class ExprUnformatted extends SimpleExpression<String> {

	static {
		Skript.registerExpression(ExprUnformatted.class, String.class, ExpressionType.PROPERTY, "(unformatted|plain) %components%");
	}

	private Expression<ComponentWrapper> inputs;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		inputs = (Expression<ComponentWrapper>) expressions[0];
		return true;
	}

	@Override
	protected String @Nullable [] get(Event event) {
		ComponentWrapper[] inputs = this.inputs.getArray(event);
		String[] unformatted = new String[inputs.length];
		PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();
		for (int i = 0; i < inputs.length; i++) {
			unformatted[i] = serializer.serialize(inputs[i].getComponent());
		}
		return unformatted;
	}

	@Override
	public boolean isSingle() {
		return inputs.isSingle();
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	public String toString(@org.eclipse.jdt.annotation.Nullable Event event, boolean debug) {
		return "unformatted " + inputs.toString(event, debug);
	}

}
