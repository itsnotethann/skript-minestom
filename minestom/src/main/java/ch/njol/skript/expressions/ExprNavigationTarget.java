package ch.njol.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.pathfinding.Navigator;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;

@Name("Navigation Target")
@Description("The navigation target of an entity. Minestom's pathfinding system isn't great right now.")
@Examples("set navigation target of {_entity} to player")
public class ExprNavigationTarget extends SimplePropertyExpression<EntityCreature, Point> {

	static {
		register(ExprNavigationTarget.class, Point.class, "(path|navigat(or|ion)) target", "entitycreatures");
	}

	@Override
	public @Nullable Point convert(EntityCreature from) {
		return from.getNavigator().getPathPosition();
	}

	@Override
	public Class<?> @org.jspecify.annotations.Nullable [] acceptChange(Changer.ChangeMode mode) {
		return switch (mode) {
			case SET, RESET,DELETE -> CollectionUtils.array(Point.class);
			default -> null;
		};
	}

	@Override
	public void change(Event event, @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) throws UnsupportedOperationException {
		EntityCreature[] entities = getExpr().getArray(event);
		Point target = delta == null ? null : ((Point) delta[0]);
		for (EntityCreature entity : entities) {
			Navigator navigator = entity.getNavigator();
			if (mode == Changer.ChangeMode.RESET || mode == Changer.ChangeMode.DELETE) {
				navigator.setPathTo(null);
				continue;
			}
			if (target == null) return;
			navigator.setPathTo(target);
		}
	}

	@Override
	protected String getPropertyName() {
		return "navigation target";
	}

	@Override
	public Class<? extends Point> getReturnType() {
		return Point.class;
	}

}
