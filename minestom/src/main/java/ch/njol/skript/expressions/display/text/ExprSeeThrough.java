package ch.njol.skript.expressions.display.text;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;

@Name("See Through")
@Description("The see through state of a text display entity.")
@Examples("set see through of targeted entity to true")
public class ExprSeeThrough extends SimplePropertyExpression<Entity, Boolean> {

	static {
		register(ExprSeeThrough.class, Boolean.class, "see through [(property|state)]", "entities");
	}

	@Override
	public @Nullable Boolean convert(Entity from) {
		if (!(from.getEntityMeta() instanceof TextDisplayMeta meta)) return null;
		return meta.isSeeThrough();
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
			if (!(e.getEntityMeta() instanceof TextDisplayMeta meta)) continue;
			switch (mode) {
				case SET -> {
					if (state == null) return;
					meta.setSeeThrough(state);
				}
				case RESET -> meta.setSeeThrough(false);
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "see through property";
	}

	@Override
	public Class<? extends Boolean> getReturnType() {
		return Boolean.class;
	}

}
