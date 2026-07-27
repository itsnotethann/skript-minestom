package ch.njol.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.MetadataDef;
import net.minestom.server.entity.metadata.cube.SlimeMeta;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;


@Name("Slime Size")
@Description("The size of a slime entity.")
@Examples("set slime size of {_entity} to 3")
public class ExprSlimeSize extends SimplePropertyExpression<Entity, Integer> {

	static {
		register(ExprSlimeSize.class, Integer.class, "slime size", "entities");
	}

	@Override
	public @Nullable Integer convert(Entity from) {
		if (from.getEntityMeta() instanceof SlimeMeta slimeMeta) return slimeMeta.getSize();
		return null;
	}

	@Override
	public Class<?> @org.jspecify.annotations.Nullable [] acceptChange(Changer.ChangeMode mode) {
		return switch (mode) {
			case SET, REMOVE, ADD, RESET -> CollectionUtils.array(Integer.class);
			default -> null;
		};
	}

	@Override
	public void change(Event event, @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) throws UnsupportedOperationException {
		Entity[] entities = getExpr().getArray(event);
		Integer size = delta == null ? null : (Integer) delta[0];
		for (Entity entity : entities) {
			if (!(entity.getEntityMeta() instanceof SlimeMeta slimeMeta)) continue;
			if (mode == Changer.ChangeMode.RESET) {
				slimeMeta.setSize(MetadataDef.Slime.SIZE.defaultValue());
				continue;
			}
			if (size == null) return;
			switch (mode) {
				case ADD -> slimeMeta.setSize(slimeMeta.getSize()+size);
				case REMOVE -> slimeMeta.setSize(slimeMeta.getSize()-size);
				case SET -> slimeMeta.setSize(size);
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "slime size";
	}

	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}

}
