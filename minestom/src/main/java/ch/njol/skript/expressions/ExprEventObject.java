package ch.njol.skript.expressions;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.lang.util.SimpleLiteral;
import ch.njol.skript.localization.Noun;
import ch.njol.skript.registrations.Classes;
import net.minestom.server.command.CommandSender;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.RetainingLogHandler;
import ch.njol.skript.log.SkriptLogger;
import ch.njol.util.Kleenean;

/**
 * @author Peter Güttinger
 */
@Name("Creature/Entity/Player/Projectile/Villager/Powered Creeper/etc.")
@Description({"The entity involved in an event (an entity is a player, a creature or an inanimate object like ignited TNT, a dropped item or an arrow).",
	"You can use the specific type of the entity that's involved in the event, e.g. in a 'death of a creeper' event you can use 'the creeper' instead of 'the entity'."})
@Since("1.0")
public class ExprEventObject extends SimpleExpression<Object> {
	static {
		Skript.registerExpression(ExprEventObject.class, Object.class, ExpressionType.PATTERN_MATCHES_EVERYTHING, "[the] [event-]<.+>");
	}

	@SuppressWarnings("null")
	private ClassInfo<?> type;

	@SuppressWarnings("null")
	private EventValueExpression<Object> entity;

	@Override
	public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		final RetainingLogHandler log = SkriptLogger.startRetainingLog();
		try {
			String input = parseResult.regexes.getFirst().group();
			if (Noun.isIndefiniteArticle(input)) return false;
			type = Classes.getClassInfoFromUserInput(input);
			log.clear();
			log.printLog();
			if (type == null || (!Entity.class.isAssignableFrom(type.getC()) && !type.getC().equals(CommandSender.class))) return false;
		} finally {
			log.stop();
		}
		entity = new EventValueExpression<>(type.getC());
		return entity.init();
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends Object> getReturnType() {
		return type.getC();
	}

	@Override
	@Nullable
	protected Object[] get(final Event e) {
		final Object[] es = entity.getArray(e);
		if (es.length == 0 || type.getC().isAssignableFrom(es[0].getClass()))
			return es;
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public <R> Expression<? extends R> getConvertedExpression(Class<R>... to) {
		for (Class<R> t : to) {
			if (t.equals(ClassInfo.class)) return new SimpleLiteral<>((R) type, false);
			if (t.equals(EntityType.class) && Player.class.isAssignableFrom(type.getC())) {
				return new SimpleLiteral<>((R) EntityType.PLAYER, false);
			}
		}
		return super.getConvertedExpression(to);
	}

	@Override
	public boolean setTime(int time) {
		// Allows using 'past' / 'future' event-entitydata if they're registered
		return entity.setTime(time);
	}

	@Override
	public String toString(final @Nullable Event e, final boolean debug) {
		return "the " + type;
	}

}
