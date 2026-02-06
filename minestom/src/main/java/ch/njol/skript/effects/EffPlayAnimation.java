package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.network.packet.server.play.EntityAnimationPacket;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

@Name("Play Animation")
@Description("Plays an animation for the given entities.")
@Examples("play animation \"swing_main_hand\" for all players")
public class EffPlayAnimation extends Effect {

	static {
		Skript.registerEffect(EffPlayAnimation.class, "play %animations% animation[s] on %entities% [(to|for) %-players%]");
	}

	private Expression<EntityAnimationPacket.Animation> animations;
	private Expression<Entity> entities;
	@Nullable
	private Expression<Player> players;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		animations = (Expression<EntityAnimationPacket.Animation>) expressions[0];
		entities = (Expression<Entity>) expressions[1];
		players = (Expression<Player>) expressions[2];
		return true;
	}

	@Override
	protected void execute(Event event) {
		for (EntityAnimationPacket.Animation animation : animations.getArray(event)) {
			for (Entity entity : entities.getArray(event)) {
				EntityAnimationPacket packet = new EntityAnimationPacket(entity.getEntityId(), animation);
				if (players == null) entity.getInstance().sendGroupedPacket(packet);
				else {
					for (Player player : players.getArray(event)) {
						player.sendPacket(packet);
					}
				}
			}
		}
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "play " + animations.toString(event, debug) + " animations on " + entities.toString(event, debug)
			+ (players == null ? "" : " " + players.toString(event, debug)) ;
	}

}
