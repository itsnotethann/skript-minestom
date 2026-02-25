package ch.njol.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.metadata.other.InteractionMeta;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;

@Name("Response")
@Description("The response state of an interaction entity.")
@Examples("set response state of {_interaction} to true")
public class ExprResponse extends SimplePropertyExpression<Entity, Boolean> {

	static {
		register(ExprResponse.class, Boolean.class, "respons(e|iveness) [state]", "entities");
	}

	@Override
	public @Nullable Boolean convert(Entity from) {
		if (!(from.getEntityMeta() instanceof InteractionMeta meta)) return null;
		return meta.getResponse();
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
			if (!(e.getEntityMeta() instanceof InteractionMeta meta)) continue;
			switch (mode) {
				case SET -> {
					if (state == null) return;
					meta.setResponse(state);
				}
				case RESET -> meta.setResponse(false);
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "response state";
	}

	@Override
	public Class<? extends Boolean> getReturnType() {
		return Boolean.class;
	}

}
