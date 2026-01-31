package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

public class EffPlaySound extends Effect {

	static {
		Skript.registerEffect(EffPlaySound.class,
			"play %sounds% %directions% %points% (to|for) %players%",
			"play %sounds% [%directions% %points%] [in [(world|instance)] %instances%]",
			"play %sounds% (on|from) %entities% [(to|for) %-players%]");
	}

	private Expression<Sound> sounds;
	@Nullable
	private Expression<Point> points;
	@Nullable
	private Expression<Player> players;
	@Nullable
	private Expression<Instance> instances;
	@Nullable
	private Expression<Entity> entities;

	private int pattern;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		pattern = matchedPattern;
		sounds = (Expression<Sound>) expressions[0];
		if (matchedPattern != 2) {
			points = Direction.combine((Expression<? extends Direction>) expressions[1], (Expression<? extends Point>) expressions[2]);
			if (matchedPattern == 0) players = (Expression<Player>) expressions[3];
			else instances = (Expression<Instance>) expressions[3];
		} else {
			entities = (Expression<Entity>) expressions[1];
			players = (Expression<Player>) expressions[2];
		}
		return true;
	}

	@Override
	protected void execute(Event event) {
		for (Sound sound : sounds.getArray(event)) {
			switch (pattern) {
				case 0 -> {
					assert players != null;
					assert points != null;
					for (Player player : players.getArray(event)) {
						for (Point point : points.getArray(event)) {
							player.playSound(sound, point);
						}
					}
				}
				case 1 -> {
					assert points != null;
					assert instances != null;
					for (Point point : points.getArray(event)) {
						for (Instance instance : instances.getArray(event)) {
							instance.playSound(sound, point);
						}
					}
				}
				case 2 -> {
					assert entities != null;
					for (Entity entity : entities.getArray(event)) {
						if (players == null) entity.getInstance().playSound(sound, entity);
						else {
							for (Player player : players.getArray(event)) {
								player.playSound(sound, entity);
							}
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("DataFlowIssue")
	@Override
	public String toString(@Nullable Event event, boolean debug) {
		String toString = "play sound " + sounds.toString(event, debug);
		return switch (pattern) {
			case 0 -> toString + points.toString(event, debug) + " to " + players.toString(event, debug);
			case 1 -> toString + points.toString(event, debug) + " in instance " + instances.toString(event, debug);
			case 2 -> toString + " from " + entities.toString(event, debug) + (players == null ? "" : " " + players.toString(event, debug));
			default -> throw new IllegalStateException("Unexpected value: " + pattern);
		};
	}

}
