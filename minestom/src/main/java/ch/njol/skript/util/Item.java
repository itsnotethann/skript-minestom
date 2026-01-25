package ch.njol.skript.util;

import net.minestom.server.item.ItemStack;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

public class Item {

	private final AtomicReference<ItemStack> item;

	public Item(ItemStack item) {
		this.item = new AtomicReference<>(item);
	}

	public void modify(UnaryOperator<ItemStack> modifyFunction) {
		modify(modifyFunction, false);
	}

	public void modify(UnaryOperator<ItemStack> modifyFunction, boolean attemptNotifyContainer) {
		item.updateAndGet(modifyFunction);
	}

	public ItemStack getItem() {
		return item.get();
	}

	public Item copy() {
		return new Item(getItem());
	}

	public static Item[] from(ItemStack[] stacks) {
		int length = stacks.length;
		Item[] items = new Item[length];
		for (int i = 0; i < length; i++) {
			items[i] = new Item(stacks[i]);
		}
		return items;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Item item1 = (Item) o;
		return Objects.equals(getItem(), item1.getItem());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getItem());
	}

}
