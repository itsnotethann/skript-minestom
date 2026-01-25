package ch.njol.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.entity.Entity;
import net.minestom.server.instance.Instance;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

public class ExprInstance extends SimplePropertyExpression<Entity, Instance> {

	static {
		register(ExprInstance.class, Instance.class, "(instance|world)", "entities");
	}

	@Override
	public @Nullable Instance convert(Entity from) {
		return from.getInstance();
	}

	@SuppressWarnings("DataFlowIssue")
	@Override
	@Nullable
	public Class<?>[] acceptChange(Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET) return CollectionUtils.array(Instance.class);
		return null;
	}

	@Override
	public void change(Event event, @Nullable Object[] delta, Changer.ChangeMode mode) throws UnsupportedOperationException {
		if (delta[0] == null) return;
		Instance instance = (Instance) delta[0];
		for (Entity e : getExpr().getArray(event)) {
			e.setInstance(instance);
		}
	}

	@Override
	protected String getPropertyName() {
		return "instance";
	}

	@Override
	public Class<? extends Instance> getReturnType() {
		return Instance.class;
	}

}
