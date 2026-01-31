package ch.njol.skript.expressions.display.abstr;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import org.bukkit.event.Event;

public class ExprBillboardConstraints extends SimplePropertyExpression<Entity, AbstractDisplayMeta.BillboardConstraints> {

	static {
		register(ExprBillboardConstraints.class, AbstractDisplayMeta.BillboardConstraints.class,
			"bill[ ]board [render] constraint[s]", "entities");
	}

	@Override
	public AbstractDisplayMeta.@org.jspecify.annotations.Nullable BillboardConstraints convert(Entity from) {
		if (!(from.getEntityMeta() instanceof AbstractDisplayMeta meta)) return null;
		return meta.getBillboardRenderConstraints();
	}

	@Override
	public @org.eclipse.jdt.annotation.Nullable Class<?>[] acceptChange(Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) return CollectionUtils.array(AbstractDisplayMeta.BillboardConstraints.class);
		return null;
	}

	@SuppressWarnings("ConstantValue")
	@Override
	public void change(Event event, @org.jspecify.annotations.Nullable @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) {
		AbstractDisplayMeta.BillboardConstraints constraints = delta == null ? null : (AbstractDisplayMeta.BillboardConstraints) delta[0];
		for (Entity entity : getExpr().getArray(event)) {
			if (!(entity.getEntityMeta() instanceof AbstractDisplayMeta meta)) continue;
			switch (mode) {
				case SET -> {
					if (constraints == null) return;
					meta.setBillboardRenderConstraints(constraints);
				}
				case RESET -> meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.FIXED);
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "billboard render constraints";
	}

	@Override
	public Class<? extends AbstractDisplayMeta.BillboardConstraints> getReturnType() {
		return AbstractDisplayMeta.BillboardConstraints.class;
	}

}
