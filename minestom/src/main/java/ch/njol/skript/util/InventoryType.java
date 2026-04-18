package ch.njol.skript.util;

import org.jetbrains.annotations.Nullable;

public enum InventoryType {

	CHEST_1_ROW(net.minestom.server.inventory.InventoryType.CHEST_1_ROW),
	CHEST_2_ROW(net.minestom.server.inventory.InventoryType.CHEST_3_ROW),
	CHEST_3_ROW(net.minestom.server.inventory.InventoryType.CHEST_3_ROW),
	CHEST_4_ROW(net.minestom.server.inventory.InventoryType.CHEST_4_ROW),
	CHEST_5_ROW(net.minestom.server.inventory.InventoryType.CHEST_5_ROW),
	CHEST_6_ROW(net.minestom.server.inventory.InventoryType.CHEST_6_ROW),
	WINDOW_3X3(net.minestom.server.inventory.InventoryType.WINDOW_3X3),
	CRAFTER_3X3(net.minestom.server.inventory.InventoryType.CRAFTER_3X3),
	ANVIL(net.minestom.server.inventory.InventoryType.ANVIL),
	BEACON(net.minestom.server.inventory.InventoryType.BEACON),
	BLAST_FURNACE(net.minestom.server.inventory.InventoryType.BLAST_FURNACE),
	BREWING_STAND(net.minestom.server.inventory.InventoryType.BREWING_STAND),
	CRAFTING(net.minestom.server.inventory.InventoryType.CRAFTING),
	ENCHANTMENT(net.minestom.server.inventory.InventoryType.ENCHANTMENT),
	FURNACE(net.minestom.server.inventory.InventoryType.FURNACE),
	GRINDSTONE(net.minestom.server.inventory.InventoryType.GRINDSTONE),
	HOPPER(net.minestom.server.inventory.InventoryType.HOPPER),
	LECTERN(net.minestom.server.inventory.InventoryType.LECTERN),
	LOOM(net.minestom.server.inventory.InventoryType.LOOM),
	MERCHANT(net.minestom.server.inventory.InventoryType.MERCHANT),
	SHULKER_BOX(net.minestom.server.inventory.InventoryType.SHULKER_BOX),
	SMITHING(net.minestom.server.inventory.InventoryType.SMITHING),
	SMOKER(net.minestom.server.inventory.InventoryType.SMOKER),
	CARTOGRAPHY(net.minestom.server.inventory.InventoryType.CARTOGRAPHY),
	STONE_CUTTER(net.minestom.server.inventory.InventoryType.STONE_CUTTER),
	PLAYER(null);

	private final net.minestom.server.inventory.InventoryType minestomType;

	InventoryType(net.minestom.server.inventory.InventoryType minestomType) {
		this.minestomType = minestomType;
	}

	public @Nullable net.minestom.server.inventory.InventoryType getMinestomType() {
		return minestomType;
	}

	public static @Nullable InventoryType of(net.minestom.server.inventory.InventoryType minestomType) {
		for (InventoryType value : values()) {
			if (value.minestomType == minestomType) return value;
		}
		return null;
	}

}
