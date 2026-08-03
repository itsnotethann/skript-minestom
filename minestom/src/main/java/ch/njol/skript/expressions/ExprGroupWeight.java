package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.hapily04.skriptminestom.SkriptMinestom;
import net.luckperms.api.model.group.Group;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Group Weight")
@Description("The weight of a LuckPerms group.")
@Examples("set {_weight} to weight of group named \"default\"")
public class ExprGroupWeight extends PropertyExpression<String, Integer> {

	static {
		Skript.registerExpression(ExprGroupWeight.class, Integer.class, ExpressionType.PROPERTY,
			"weight of group[s] [named] %strings%",
			"group[s] [named] %strings%'[s] weight");
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		setExpr((Expression<? extends String>) expressions[0]);
		return true;
	}

	@Override
	protected Integer[] get(Event event, String[] source) {
		List<Integer> weights = new ArrayList<>();
		for (String groupName : source) {
			Group group = SkriptMinestom.getLuckPerms().getGroupManager().getGroup(groupName);
			if (group == null) continue;
			weights.add(group.getWeight().orElse(0));
		}
		return weights.toArray(new Integer[0]);
	}

	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "weight of groups named " + getExpr().toString(event, debug);
	}

}
