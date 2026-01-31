package ch.njol.skript.util;

import net.minestom.server.entity.EquipmentSlot;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.inventory.EquipmentHandler;
import net.minestom.server.item.ItemStack;

import java.util.function.UnaryOperator;

public class Slot extends Item {

	private final Updater updater;

	public Slot(ItemStack item, Updater updater) {
		super(item);
		this.updater = updater;
	}

	public Slot(ItemStack item, AbstractInventory container, int slot) {
		this(item ,new InventoryUpdater(container, slot));
	}

	public Slot(ItemStack item, EquipmentHandler handler, EquipmentSlot slot) {
		this(item, new EquipmentUpdater(handler, slot));
	}

	@Override
	public void modify(UnaryOperator<ItemStack> modifyFunction, boolean notifyContainer) {
		ItemStack containerSlotItem = updater.getCurrentItem();
		ItemStack preModificationItem = getItem();
		super.modify(modifyFunction, notifyContainer);
		if (notifyContainer && preModificationItem.equals(containerSlotItem)) updater.update(getItem());
	}

	public interface Updater {

		void update(ItemStack item);

		ItemStack getCurrentItem();

	}

	static class InventoryUpdater implements Updater {

		private final AbstractInventory container;
		private final int slot;

		public InventoryUpdater(AbstractInventory container, int slot) {
			this.container = container;
			this.slot = slot;
		}


		@Override
		public void update(ItemStack item) {
			container.setItemStack(slot, item);
		}

		@Override
		public ItemStack getCurrentItem() {
			return container.getItemStack(slot);
		}

	}

	static class EquipmentUpdater implements Updater {

		private final EquipmentHandler container;
		private final EquipmentSlot slot;

		public EquipmentUpdater(EquipmentHandler container, EquipmentSlot slot) {
			this.container = container;
			this.slot = slot;
		}

		@Override
		public void update(ItemStack item) {
			container.setEquipment(slot, item);
		}

		@Override
		public ItemStack getCurrentItem() {
			return container.getEquipment(slot);
		}

	}

}
