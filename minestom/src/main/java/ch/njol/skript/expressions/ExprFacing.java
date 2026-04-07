package ch.njol.skript.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.BlockFace;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.instance.block.Block;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import static ch.njol.skript.util.VectorMath.*;

/**
 * @author Peter Güttinger
 */
@Name("Facing")
@Description("The facing of an entity or block, i.e. exactly north, south, east, west, up or down (unlike <a href='#ExprDirection'>direction</a> which is the exact direction, e.g. '0.5 south and 0.7 east')")
@Examples({"# makes a bridge",
	"loop blocks from the block below the player in the horizontal facing of the player:",
	"\tset loop-block to cobblestone"})
@Since("1.4")
public class ExprFacing extends SimplePropertyExpression<Object, Direction> {

	static {
		register(ExprFacing.class, Direction.class, "(1¦horizontal|) facing", "entities/blocks");
	}

	private boolean horizontal;

	@Override
	public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		horizontal = parseResult.mark == 1;
		return super.init(exprs, matchedPattern, isDelayed, parseResult);
	}

	@SuppressWarnings("deprecation")
	@Override
	@Nullable
	public Direction convert(final Object o) {
		if (o instanceof Block) {
			BlockFace face = Direction.getFacing((Block) o);
			return face == null ? null : new Direction(face.toDirection());
		} else if (o instanceof Entity) {
			return new Direction(Direction.getFacing(((Entity) o).getPosition(), horizontal), 1);
		}
		assert false;
		return null;
	}

	@Override
	protected String getPropertyName() {
		return (horizontal ? "horizontal " : "") + "facing";
	}

	@Override
	public Class<Direction> getReturnType() {
		return Direction.class;
	}

	@Override
	@Nullable
	public Class<?>[] acceptChange(final ChangeMode mode) {
		System.out.println(Entity.class + " " + getExpr().getReturnType());
		if (!Entity.class.isAssignableFrom(getExpr().getReturnType()))
			return null;
		System.out.println("here");
		if (mode == ChangeMode.SET)
			return CollectionUtils.array(Direction.class);
		System.out.println("here2");
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void change(final Event e, final @Nullable Object[] delta, final ChangeMode mode) throws UnsupportedOperationException {
		assert mode == ChangeMode.SET;
		assert delta != null;

		final Entity entity = (Entity) getExpr().getSingle(e);
		if (entity == null)
			return;
		assert delta[0] != null;
		float[] yawPitch = getYawPitch((Direction) delta[0]);
		entity.setView(yawPitch[0], yawPitch[1]);
	}

	private float[] getYawPitch(Direction d) {
		Vec vec = d.getDirection();
		float yaw = skriptYaw(getYaw(vec));
		float pitch = skriptPitch(getPitch(vec));
		return new float[]{yaw, pitch};
	}

}
