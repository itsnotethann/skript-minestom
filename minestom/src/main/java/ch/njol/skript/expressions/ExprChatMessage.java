package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.events.wrapper.PlayerChatWrapper;
import ch.njol.skript.lang.EventRestrictedSyntax;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;

public class ExprChatMessage extends SimpleExpression<String> implements EventRestrictedSyntax {

	static {
		Skript.registerExpression(ExprChatMessage.class, String.class, ExpressionType.SIMPLE, "[chat] message");
	}

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		return true;
	}

	@Override
	protected @Nullable String[] get(Event event) {
		return new String[]{((PlayerChatWrapper) event).getEvent().getRawMessage()};
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
	public String toString(@org.eclipse.jdt.annotation.Nullable Event event, boolean debug) {
		return "chat message";
	}

	@Override
	public Class<? extends Event>[] supportedEvents() {
		return new Class[]{PlayerChatWrapper.class};
	}

}