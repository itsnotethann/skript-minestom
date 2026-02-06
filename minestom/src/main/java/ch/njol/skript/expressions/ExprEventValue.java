package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.expressions.base.WrapperExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Utils;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

@Name("Event Value")
@Description("Returns the value of a certain type in an event (e.g. 'player', 'event-string').")
@Examples("on chat:\n\tset {_m} to event-string")
public class ExprEventValue extends WrapperExpression<Object> {

	static {
		Skript.registerExpression(ExprEventValue.class, Object.class, ExpressionType.EVENT, "[event-]%*classinfo%");
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		ClassInfo<?> classInfo = ((Literal<ClassInfo<?>>) expressions[0]).getSingle();
		Class<?> clazz = classInfo.getC();
		if (Utils.getEnglishPlural(parseResult.expr).getSecond()) clazz = CollectionUtils.arrayType(clazz);
		EventValueExpression<?> eventValueExpression = new EventValueExpression<>(clazz);
		setExpr(eventValueExpression);
		return eventValueExpression.init();
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return getExpr().toString(event, debug);
	}

}
