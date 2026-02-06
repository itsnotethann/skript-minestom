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
import ch.njol.skript.util.Enchantment;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Name("Chat Recipients")
@Description("The recipients of a chat message in a chat event.")
@Examples("remove player from chat recipients")
public class ExprChatRecipients  extends SimpleExpression<Player> implements EventRestrictedSyntax {

	static {
		Skript.registerExpression(ExprChatRecipients.class, Player.class, ExpressionType.SIMPLE, "chat recipients");
	}

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		return true;
	}

	@Override
	protected @Nullable Player[] get(Event event) {
		return ((PlayerChatWrapper) event).getEvent().getRecipients().toArray(new Player[0]);
	}

	@Override
	public @org.eclipse.jdt.annotation.Nullable Class<?>[] acceptChange(Changer.ChangeMode mode) {
		return switch (mode) {
			case ADD, SET, REMOVE, RESET -> CollectionUtils.array(Player[].class);
			default -> null;
		};
	}

	@SuppressWarnings("ConstantValue")
	@Override
	public void change(Event event, @Nullable @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) {
		List<Player> recipients = (List<Player>) ((PlayerChatWrapper) event).getEvent().getRecipients();
		Player[] players = Arrays.copyOf(delta, delta.length, Player[].class);
		switch (mode) {
			case SET -> {
				recipients.clear();
				recipients.addAll(List.of(players));
			}
			case ADD -> {
				for (Player player : players) {
					if (recipients.contains(player)) continue;
					recipients.add(player);
				}
			}
			case REMOVE -> {
				for (Player player : players) {
					recipients.remove(player);
				}
			}
			case RESET -> {
				recipients.clear();
				recipients.addAll(MinecraftServer.getConnectionManager().getOnlinePlayers());
			}
		}
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends Player> getReturnType() {
		return Player.class;
	}

	@Override
	public String toString(@org.eclipse.jdt.annotation.Nullable Event event, boolean debug) {
		return "chat recipients";
	}

	@Override
	public Class<? extends Event>[] supportedEvents() {
		return new Class[]{PlayerChatWrapper.class};
	}

}