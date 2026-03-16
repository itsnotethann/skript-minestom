package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.events.wrapper.ServerListPingWrapper;
import ch.njol.skript.lang.EventRestrictedSyntax;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.ping.Status;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;

public class ExprMOTDChatSecurity extends SimpleExpression<Boolean> implements EventRestrictedSyntax {

	static {
		Skript.registerExpression(ExprMOTDChatSecurity.class, Boolean.class, ExpressionType.EVENT,
			"motd chat security");
	}

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		return true;
	}

	@Override
	protected @Nullable Boolean[] get(Event event) {
		ServerListPingEvent e = ((ServerListPingWrapper) event).getEvent();
		return new Boolean[]{e.getStatus().enforcesSecureChat()};
	}

	@Override
	public @org.eclipse.jdt.annotation.Nullable Class<?>[] acceptChange(Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.RESET || mode == Changer.ChangeMode.SET) return CollectionUtils.array(Boolean.class);
		return null;
	}

	@Override
	public void change(Event event, @Nullable @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) {
		ServerListPingEvent e = ((ServerListPingWrapper) event).getEvent();
		Status currentStatus = e.getStatus();
		boolean secureChat;
		if (mode == Changer.ChangeMode.RESET) secureChat = false;
		else {
			Boolean security = delta == null ? null : (Boolean) delta[0];
			if (security == null) return;
			secureChat = security;
		}
		Status newStatus = Status.builder(currentStatus)
			.enforcesSecureChat(secureChat)
			.build();
		e.setStatus(newStatus);
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends Boolean> getReturnType() {
		return Boolean.class;
	}

	@Override
	public String toString(@org.eclipse.jdt.annotation.Nullable Event event, boolean debug) {
		return "motd chat security";
	}

	@Override
	public Class<? extends Event>[] supportedEvents() {
		return new Class[]{ServerListPingWrapper.class};
	}

}

