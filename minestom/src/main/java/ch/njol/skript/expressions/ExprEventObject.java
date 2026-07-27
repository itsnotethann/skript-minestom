package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.lang.util.SimpleLiteral;
import ch.njol.skript.localization.Noun;
import ch.njol.skript.log.RetainingLogHandler;
import ch.njol.skript.log.SkriptLogger;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.Utils;
import ch.njol.util.Kleenean;
import net.minestom.server.command.CommandSender;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

/**
 * @author Peter Güttinger
 */
@Name("Event Entity/Player/CommandSender")
@Description("The entity/player/commandsender involved in an event, allowing the exclusion of 'event-'.")
@Examples("""
	on join:
		send "Welcome to the server" to player # 'player'""")
@Since("1.0")
public class ExprEventObject extends SimpleExpression<Object> {
	static {
		Skript.registerExpression(ExprEventObject.class, Object.class, ExpressionType.PATTERN_MATCHES_EVERYTHING, "[the] [event-]<.+>");
	}

	@SuppressWarnings("null")
	private ClassInfo<?> type;

	@SuppressWarnings("null")
	private EventValueExpression<Object> eventValue;

	@Override
	public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		final RetainingLogHandler log = SkriptLogger.startRetainingLog();
		String input = parseResult.regexes.getFirst().group();
		Class<?> clazz;
		try {
			if (Noun.isIndefiniteArticle(input)) return false;
			type = Classes.getClassInfoFromUserInput(input);
			log.clear();
			log.printLog();
			if (type == null) return false;
			clazz = type.getC();
			if (!Entity.class.isAssignableFrom(clazz) && !clazz.equals(CommandSender.class)) return false;
		} finally {
			log.stop();
		}
		eventValue = new EventValueExpression<>(Utils.isPlural(input).plural() ? clazz.arrayType() : clazz);
		return eventValue.init();
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<?> getReturnType() {
		return type.getC();
	}

	@Override
	@Nullable
	protected Object[] get(final Event e) {
		final Object[] es = eventValue.getArray(e);
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
		return eventValue.setTime(time);
	}

	@Override
	public String toString(final @Nullable Event e, final boolean debug) {
		return "the " + type;
	}

}
