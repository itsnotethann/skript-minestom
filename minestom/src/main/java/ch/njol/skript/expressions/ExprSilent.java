
package ch.njol.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.entity.Entity;
import net.minestom.server.network.packet.server.play.ChangeGameStatePacket;
import net.minestom.server.network.packet.server.play.WorldEventPacket;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;

public class ExprSilent extends SimplePropertyExpression<Entity, Boolean> {

	static {
		register(ExprSilent.class, Boolean.class, "silen(t property|ce)", "entities");
	}

	@Override
	public @Nullable Boolean convert(Entity from) {
		return from.isSilent();
	}

	@Override
	public @org.eclipse.jdt.annotation.Nullable Class<?>[] acceptChange(Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) return CollectionUtils.array(Boolean.class);
		return null;
	}

	@SuppressWarnings("ConstantValue")
	@Override
	public void change(Event event, @Nullable @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) {
		Boolean state = delta == null ? null : (Boolean) delta[0];
		for (Entity e : getExpr().getArray(event)) {
			switch (mode) {
				case SET -> {
					if (state == null) return;
					e.setSilent(state);
					e.sendPacketsToViewers(new ChangeGameStatePacket(ChangeGameStatePacket.Reason.DEMO_EVENT, )));
				}
				case RESET -> e.setSilent(false);
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "silent property";
	}

	@Override
	public Class<? extends Boolean> getReturnType() {
		return Boolean.class;
	}

}
