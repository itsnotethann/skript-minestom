package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.events.wrapper.ServerListPingWrapper;
import ch.njol.skript.lang.EventRestrictedSyntax;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.ping.Status;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;


@Name("MOTD Player Count")
@Description("The online or maximum player count in the server list ping event.")
@Examples("set motd max player count to 100")
public class ExprMOTDPlayerCount extends SimpleExpression<Integer> implements EventRestrictedSyntax {

	static {
		Skript.registerExpression(ExprMOTDPlayerCount.class, Integer.class, ExpressionType.EVENT,
			"motd ((max:max[imum])|online) player(s|[ ]count)");
	}

	private boolean max;

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		max = parseResult.hasTag("max");
		return true;
	}

	@Override
	protected @Nullable Integer[] get(Event event) {
		ServerListPingEvent e = ((ServerListPingWrapper) event).getEvent();
		Status.PlayerInfo playerInfo = getPlayerInfo(e.getStatus());
		return new Integer[]{max ? playerInfo.maxPlayers() : playerInfo.onlinePlayers()};
	}

	@Override
	public @org.eclipse.jdt.annotation.Nullable Class<?>[] acceptChange(Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.RESET || mode == Changer.ChangeMode.SET) return CollectionUtils.array(Integer.class);
		return null;
	}

	@Override
	public void change(Event event, @Nullable @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) {
		ServerListPingEvent e = ((ServerListPingWrapper) event).getEvent();
		Status currentStatus = e.getStatus();
		Status.PlayerInfo oldPlayerInfo = getPlayerInfo(currentStatus);
		int newOnlinePlayerCount = oldPlayerInfo.onlinePlayers();
		int newMaxPlayerCount = oldPlayerInfo.maxPlayers();
		if (mode == Changer.ChangeMode.RESET) {
			int playerCount = MinecraftServer.getConnectionManager().getOnlinePlayerCount();
			if (!max) newOnlinePlayerCount = playerCount;
			else newMaxPlayerCount = playerCount+1;
		} else {
			Integer count = delta == null ? null : (Integer) delta[0];
			if (count == null) return;
			if (!max) newOnlinePlayerCount = count;
			else newMaxPlayerCount = count;
		}
		Status newStatus = Status.builder(currentStatus)
			.playerInfo(new Status.PlayerInfo(newOnlinePlayerCount, newMaxPlayerCount, oldPlayerInfo.sample()))
			.build();
		e.setStatus(newStatus);
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}

	@Override
	public String toString(@org.eclipse.jdt.annotation.Nullable Event event, boolean debug) {
		return "motd " + (max ? "maximum" : "online") + " player count";
	}

	@Override
	public Class<? extends Event>[] supportedEvents() {
		return new Class[]{ServerListPingWrapper.class};
	}

	static Status.PlayerInfo getPlayerInfo(Status status) {
		Status.PlayerInfo playerInfo = status.playerInfo();
		if (playerInfo == null) {
			int onlinePlayerCount = MinecraftServer.getConnectionManager().getOnlinePlayerCount();
			playerInfo = new Status.PlayerInfo(onlinePlayerCount, onlinePlayerCount+1);
		}
		return playerInfo;
	}

}
