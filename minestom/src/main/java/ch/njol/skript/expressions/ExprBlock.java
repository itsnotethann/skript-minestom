package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.ConvertedExpression;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.batch.AbsoluteBlockBatch;
import net.minestom.server.instance.block.Block;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

@SuppressWarnings("NotNullFieldNotInitialized")
public class ExprBlock extends SimpleExpression<Block> {

	static {
		Skript.registerExpression(ExprBlock.class, Block.class, ExpressionType.SIMPLE, "block[s] [type[s]] %direction% [%point%] [in [(world|instance)] %instance%]");
	}

	private Expression<? extends Point> pointExpr;
	private Expression<Instance> instanceExpr;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		pointExpr = Direction.combine((Expression<? extends Direction>) expressions[0], (Expression<? extends Pos>) expressions[1]);
		instanceExpr = (Expression<Instance>) expressions[2];
		return true;
	}

	@Override
	protected @Nullable Block[] get(Event event) {
		Instance instance = instanceExpr.getSingle(event);
		if (instance == null) return new Block[0];
		Point[] points = pointExpr.getArray(event);
		int length = points.length;
		Block[] blocks = new Block[length];
		for (int i = 0; i < length; i++) {
			blocks[i] = instance.getBlock(points[i]);
		}
		return blocks;
	}

	@SuppressWarnings("DataFlowIssue")
	@Override
	@Nullable
	public Class<?>[] acceptChange(Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET) return CollectionUtils.array(Block.class);
		return null;
	}

	@Override
	public void change(Event event, @Nullable Object[] delta, Changer.ChangeMode mode) throws UnsupportedOperationException {
		if (delta[0] == null) return;
		Block block = (Block) delta[0];
		Instance instance = instanceExpr.getSingle(event);
		if (instance == null) return;
		Point[] points = pointExpr.getArray(event);
		for (Point p : points) {
			if (!instance.isChunkLoaded(p)) continue;
			instance.setBlock(p, block);
		}
	}

	@Override
	public boolean isSingle() {
		return pointExpr.isSingle();
	}

	@Override
	public Class<? extends Block> getReturnType() {
		return Block.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "block at " + pointExpr.toString(event, debug) + " in instance " + instanceExpr.toString(event, debug);
	}

}
