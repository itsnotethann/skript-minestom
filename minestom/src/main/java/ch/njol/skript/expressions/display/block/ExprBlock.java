package ch.njol.skript.expressions.display.block;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.instance.block.Block;
import org.bukkit.event.Event;

public class ExprBlock extends SimplePropertyExpression<Entity, Block> {

	static {
		register(ExprBlock.class, Block.class, "display block", "entities");
	}

	@Override
	public @org.jspecify.annotations.Nullable Block convert(Entity from) {
		if (!(from.getEntityMeta() instanceof BlockDisplayMeta meta)) return null;
		return meta.getBlockStateId();
	}

	@Override
	public @org.eclipse.jdt.annotation.Nullable Class<?>[] acceptChange(Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) return CollectionUtils.array(Block.class);
		return null;
	}

	@SuppressWarnings("ConstantValue")
	@Override
	public void change(Event event, @org.jspecify.annotations.Nullable @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) {
		Block block = delta == null ? null : (Block) delta[0];
		for (Entity entity : getExpr().getArray(event)) {
			if (!(entity.getEntityMeta() instanceof BlockDisplayMeta meta)) continue;
			switch (mode) {
				case SET -> {
					if (block == null) return;
					meta.setBlockState(block);
				}
				case RESET -> meta.setBlockState(Block.AIR);
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "display block";
	}

	@Override
	public Class<? extends Block> getReturnType() {
		return Block.class;
	}

}
