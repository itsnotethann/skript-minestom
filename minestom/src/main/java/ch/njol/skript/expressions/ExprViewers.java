package ch.njol.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ExprViewers extends PropertyExpression<Entity, Player> {

	static {
		register(ExprViewers.class, Player.class, "viewers", "entities");
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		setExpr((Expression<? extends Entity>) expressions[0]);
		return true;
	}

	@Override
	public @Nullable Class<?>[] acceptChange(Changer.ChangeMode mode) {
		return switch (mode) {
			case REMOVE, RESET, DELETE, ADD -> CollectionUtils.array(Player[].class);
			default -> null;
		};
	}

	@SuppressWarnings("ConstantValue")
	@Override
	public void change(Event event, @org.jspecify.annotations.Nullable @Nullable Object[] delta, Changer.ChangeMode mode) {
		Player[] players = delta == null ? new Player[0] : Arrays.copyOf(delta, delta.length, Player[].class);
		for (Entity entity : getExpr().getArray(event)) {
			if (mode == Changer.ChangeMode.DELETE || mode == Changer.ChangeMode.RESET) {
				Iterator<Player> iterator = entity.getViewers().iterator();
				//noinspection WhileLoopReplaceableByForEach // not safe to replace
				while (iterator.hasNext()) {
					entity.removeViewer(iterator.next());
				}
				continue;
			}
			for (Player p : players) {
				switch (mode) {
					case REMOVE -> entity.removeViewer(p);
					case ADD -> {
						if (entity.equals(p)) continue;
						entity.addViewer(p);
					}
				}
			}
		}
	}

	@Override
	protected Player[] get(Event event, Entity[] source) {
		List<Player> viewers = new ArrayList<>();
		for (Entity entity : source) {
			viewers.addAll(entity.getViewers());
		}
		return viewers.toArray(new Player[0]);
	}

	@Override
	public Class<? extends Player> getReturnType() {
		return Player.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "viewers of " + getExpr().toString(event, debug);
	}

}

