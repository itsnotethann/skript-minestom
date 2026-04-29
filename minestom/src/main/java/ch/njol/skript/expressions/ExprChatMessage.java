package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.events.wrapper.PlayerChatWrapper;
import ch.njol.skript.lang.EventRestrictedSyntax;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.ComponentWrapper;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import net.kyori.adventure.text.Component;
import net.minestom.server.event.player.PlayerChatEvent;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;

import static ch.njol.skript.util.ComponentWrapper.toWrapper;

@Name("Chat Message")
@Description("The raw message in a chat event.")
@Examples("broadcast the chat message")
public class ExprChatMessage extends SimpleExpression<Object> implements EventRestrictedSyntax {

	static {
		Skript.registerExpression(ExprChatMessage.class, Object.class, ExpressionType.SIMPLE, "[:raw] [chat] message");
	}

	private boolean raw;

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		raw = parseResult.hasTag("raw");
		return true;
	}

	@Override
	protected @Nullable Object[] get(Event event) {
		PlayerChatEvent e = ((PlayerChatWrapper) event).getEvent();
		if (raw) return new String[]{e.getRawMessage()};
		return new ComponentWrapper[]{toWrapper(e.getFormattedMessage())};
	}

	@Override
	public Class<?> @org.jetbrains.annotations.Nullable [] acceptChange(Changer.ChangeMode mode) {
		if (raw || mode != Changer.ChangeMode.SET) return null;
		return CollectionUtils.array(ComponentWrapper.class);
	}

	@Override
	public void change(Event event, Object @org.jetbrains.annotations.Nullable [] delta, Changer.ChangeMode mode) {
		ComponentWrapper wrapper = (ComponentWrapper) delta[0];
		if (wrapper == null) return;
		PlayerChatEvent e = ((PlayerChatWrapper) event).getEvent();
		e.setFormattedMessage(wrapper.getComponent());
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<?> getReturnType() {
		return raw ? String.class : ComponentWrapper.class;
	}

	@Override
	public String toString(@org.eclipse.jdt.annotation.Nullable Event event, boolean debug) {
		return (raw ? "raw " : "") + "chat message";
	}

	@Override
	public Class<? extends Event>[] supportedEvents() {
		return new Class[]{PlayerChatWrapper.class};
	}

}