package ch.njol.skript.expressions.display.item;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Item;
import ch.njol.skript.util.Slot;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.item.ItemStack;
import org.bukkit.event.Event;

@Name("Display Item")
@Description("The item displayed by an item display entity.")
@Examples("set display item of {_entity} to diamond sword")
public class ExprItem extends SimplePropertyExpression<Entity, Slot> {

	static {
		register(ExprItem.class, Slot.class, "display item [slot]", "entities");
	}

	@Override
	public @org.jspecify.annotations.Nullable Slot convert(Entity from) {
		if (!(from.getEntityMeta() instanceof ItemDisplayMeta meta)) return null;
		return new Slot(meta.getItemStack(), new Slot.Updater() {
			@Override
			public void update(ItemStack item) {
				meta.setItemStack(item);
			}

			@Override
			public ItemStack getCurrentItem() {
				return meta.getItemStack();
			}

			@Override
			public int getSlot() {
				return 0;
			}

			@Override
			public AbstractInventory getContainer() {
				return null;
			}
		});
	}

	@Override
	public @org.eclipse.jdt.annotation.Nullable Class<?>[] acceptChange(Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) return CollectionUtils.array(Item.class);
		return null;
	}

	@Override
	public void change(Event event, @org.jspecify.annotations.Nullable @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) {
		Item item = delta == null ? null : (Item) delta[0];
		for (Entity entity : getExpr().getArray(event)) {
			if (!(entity.getEntityMeta() instanceof ItemDisplayMeta meta)) continue;
			switch (mode) {
				case SET -> {
					if (item == null) return;
					meta.setItemStack(item.getItem());
				}
				case RESET -> meta.setItemStack(ItemStack.AIR);
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "display item";
	}

	@Override
	public Class<? extends Slot> getReturnType() {
		return Slot.class;
	}

}