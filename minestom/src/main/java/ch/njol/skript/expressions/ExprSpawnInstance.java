package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.events.wrapper.AsyncPlayerConfigurationWrapper;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.instance.Instance;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;


@Name("Spawn Instance")
@Description("The instance a player will spawn into during the player configuration event.")
@Examples("set spawn instance to {_world}")
public class ExprSpawnInstance extends SimpleExpression<Instance> {

	static {
		Skript.registerExpression(ExprSpawnInstance.class, Instance.class, ExpressionType.EVENT, "spawn[ing] (instance|world)");
	}

	private Kleenean delayed;

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		if (!getParser().isCurrentEvent(AsyncPlayerConfigurationWrapper.class)) {
			Skript.error("The 'spawn instance' expression must only be used in the player configuration event.");
			return false;
		}
		delayed = isDelayed;
		return true;
	}

	@Override
	protected @Nullable Instance[] get(Event event) {
		return new Instance[]{((AsyncPlayerConfigurationWrapper) event).getEvent().getSpawningInstance()};
	}

	@SuppressWarnings("DataFlowIssue")
	@Override
	@Nullable
	public Class<?>[] acceptChange(Changer.ChangeMode mode) {
		if (delayed != Kleenean.FALSE) {
			Skript.error("Can't set the spawn instance anymore after the event has already passed.");
			return null;
		}
		if (mode == Changer.ChangeMode.SET) return CollectionUtils.array(Instance.class);
		return null;
	}

	@Override
	public void change(Event event, @Nullable Object[] delta, Changer.ChangeMode mode) throws UnsupportedOperationException {
		if (delta[0] == null) return;
		((AsyncPlayerConfigurationWrapper) event).getEvent().setSpawningInstance((Instance) delta[0]);
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends Instance> getReturnType() {
		return Instance.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "spawning instance";
	}

}
