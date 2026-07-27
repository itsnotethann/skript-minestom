package ch.njol.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Item;
import ch.njol.skript.util.Slot;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Cursor Slot")
@Description("The item which the player has on their inventory cursor")
@Example("cursor slot of player is dirt")
@Example("set cursor slot of player to 64 diamond")
public class ExprCursorSlot extends SimplePropertyExpression<Player, Slot> {

	static {
		register(ExprCursorSlot.class, Slot.class, "cursor slot", "players");
	}

	@Override
	public @org.jspecify.annotations.Nullable Slot convert(Player from) {
		return new Slot(from.getInventory().getCursorItem(), new Slot.Updater() {

			@Override
			public void update(ItemStack item) {
				from.getInventory().setCursorItem(item);
			}

			@Override
			public ItemStack getCurrentItem() {
				return from.getInventory().getCursorItem();
			}

			@Override
			public int getSlot() {
				return -1;
			}

			@Override
			public AbstractInventory getContainer() {
				return from.getInventory();
			}
		});
	}

	@Override
	public Class<?> @org.jetbrains.annotations.Nullable [] acceptChange(Changer.ChangeMode mode) {
		return switch (mode) {
			case SET, RESET, DELETE -> CollectionUtils.array(Item.class);
			default -> null;
		};
	}

	@Override
	public void change(Event event, Object @Nullable [] delta, Changer.ChangeMode mode) {
		Player[] players = getExpr().getArray(event);
		Item item = delta == null ? null : (Item) delta[0];
		for (Player player : players) {
			PlayerInventory inventory = player.getInventory();
			if (mode == Changer.ChangeMode.RESET || mode == Changer.ChangeMode.DELETE) {
				inventory.setCursorItem(ItemStack.AIR);
				continue;
			}
			if (item == null) return;
			inventory.setCursorItem(item.getItem());
		}
	}

	@Override
	protected String getPropertyName() {
		return "cursor slot";
	}

	@Override
	public Class<? extends Slot> getReturnType() {
		return Slot.class;
	}

}
