package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.kyori.adventure.text.Component;
import net.minestom.server.world.attribute.BedRule;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class ExprBedRule extends SimpleExpression<BedRule> {

	static {
		Skript.registerExpression(ExprBedRule.class, BedRule.class, ExpressionType.COMBINED,
			"[new] bed rule with sleep [rule] %bedrulerule% (,|[and] with) [set] spawn [rule] %bedrulerule% [explode:with explosion[s]] [[and] with error [message] %-component%]");
	}

	private Expression<BedRule.Rule> sleepRule;
	private Expression<BedRule.Rule> spawnRule;
	private boolean explodes;
	@Nullable
	private Expression<Component> errorMessage;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		sleepRule = (Expression<BedRule.Rule>) expressions[0];
		spawnRule = (Expression<BedRule.Rule>) expressions[1];
		explodes = parseResult.hasTag("explode");
		errorMessage = (Expression<Component>) expressions[2];
		return true;
	}

	@Override
	protected BedRule[] get(Event event) {
		BedRule.Rule sleep = sleepRule.getSingle(event);
		if (sleep == null) return new BedRule[0];
		BedRule.Rule spawn = sleepRule.getSingle(event);
		if (spawn == null) return new BedRule[0];
		Component errorMessage = null;
		if (this.errorMessage != null) errorMessage = this.errorMessage.getSingle(event);
		return new BedRule[]{new BedRule(sleep, spawn, explodes, errorMessage)};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends BedRule> getReturnType() {
		return BedRule.class;
	}

	@Override
	public String toString(@org.eclipse.jdt.annotation.Nullable Event event, boolean debug) {
		SyntaxStringBuilder sb = new SyntaxStringBuilder(event, debug);
		sb.append("bed rule with sleep rule", sleepRule, ", set spawn rule", spawnRule);
		if (explodes) sb.append("with explosions");
		if (errorMessage != null) sb.append("with error message", errorMessage);
		return sb.toString();
	}

}

