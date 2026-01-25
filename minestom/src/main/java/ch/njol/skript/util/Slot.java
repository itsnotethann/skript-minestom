package ch.njol.skript.util;

import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.item.ItemStack;

import java.util.function.UnaryOperator;

public class Slot extends Item {

	private final AbstractInventory container;
	private final int slot;

	public Slot(ItemStack item, AbstractInventory container, int slot) {
		super(item);
		this.container = container;
		this.slot = slot;
	}

	@Override
	public void modify(UnaryOperator<ItemStack> modifyFunction, boolean notifyContainer) {
		ItemStack containerSlotItem = container == null ? null : container.getItemStack(slot);
		ItemStack preModificationItem = getItem();
		super.modify(modifyFunction, notifyContainer);
		if (notifyContainer && preModificationItem.equals(containerSlotItem)) container.setItemStack(slot, getItem());
	}

}
