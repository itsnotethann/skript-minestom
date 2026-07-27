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
@Examples("""
	on chat with priority lowest:
	    set {_message} to resolver("message", message) # resolver so they can't inject minimessage tags
	    set chat format to mm("%player% <grey>» <white><message>", {_message})
	    send chat format to console
	
	    play sound "block.comparator.click" at volume 0.5 with pitch 2 to all players""")
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