package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.sections.EffSecSendPack;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

public class ExprPackUUID extends SimpleExpression<String> {

	static {
		Skript.registerExpression(ExprPackUUID.class, String.class, ExpressionType.SIMPLE, "[resource] pack (uuid|id)");
	}

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		if (!getParser().isCurrentEvent(EffSecSendPack.ResourcePackCallbackEvent.class)) {
			Skript.error("You can only use the pack status expression in the send resource pack callback section.");
			return false;
		}
		return true;
	}

	@Override
	protected @Nullable String[] get(Event event) {
		return new String[]{((EffSecSendPack.ResourcePackCallbackEvent) event).getUuid()};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "resource pack uuid";
	}

}
