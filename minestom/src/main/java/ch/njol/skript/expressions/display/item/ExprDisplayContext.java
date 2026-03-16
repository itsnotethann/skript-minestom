package ch.njol.skript.expressions.display.item;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import org.bukkit.event.Event;

@Name("Item Display Context")
@Description("The display context of an item display entity.")
@Examples("set display context of targeted entity to head")
public class ExprDisplayContext extends SimplePropertyExpression<Entity, ItemDisplayMeta.DisplayContext> {

	static {
		register(ExprDisplayContext.class, ItemDisplayMeta.DisplayContext.class,
			"[item] display context", "entities");
	}

	@Override
	public ItemDisplayMeta.@org.jspecify.annotations.Nullable DisplayContext convert(Entity from) {
		if (!(from.getEntityMeta() instanceof ItemDisplayMeta meta)) return null;
		return meta.getDisplayContext();
	}

	@Override
	public @org.eclipse.jdt.annotation.Nullable Class<?>[] acceptChange(Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) return CollectionUtils.array(ItemDisplayMeta.DisplayContext.class);
		return null;
	}

	@SuppressWarnings("ConstantValue")
	@Override
	public void change(Event event, @org.jspecify.annotations.Nullable @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) {
		ItemDisplayMeta.DisplayContext context = delta == null ? null : (ItemDisplayMeta.DisplayContext) delta[0];
		for (Entity entity : getExpr().getArray(event)) {
			if (!(entity.getEntityMeta() instanceof ItemDisplayMeta meta)) continue;
			switch (mode) {
				case SET -> {
					if (context == null) return;
					meta.setDisplayContext(context);
				}
				case RESET -> meta.setDisplayContext(ItemDisplayMeta.DisplayContext.NONE);
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "item display context";
	}

	@Override
	public Class<? extends ItemDisplayMeta.DisplayContext> getReturnType() {
		return ItemDisplayMeta.DisplayContext.class;
	}

}
