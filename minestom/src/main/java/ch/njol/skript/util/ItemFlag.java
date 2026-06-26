package ch.njol.skript.util;

import net.minestom.server.component.DataComponent;

public enum ItemFlag {

	/*
	                    DataComponents.BANNER_PATTERNS, DataComponents.BEES, DataComponents.BLOCK_ENTITY_DATA,
                    DataComponents.BLOCK_STATE, DataComponents.BUNDLE_CONTENTS, DataComponents.CHARGED_PROJECTILES,
                    DataComponents.CONTAINER, DataComponents.CONTAINER_LOOT, DataComponents.FIREWORK_EXPLOSION,
                    DataComponents.FIREWORKS, DataComponents.INSTRUMENT, DataComponents.MAP_ID,
                    DataComponents.PAINTING_VARIANT, DataComponents.POT_DECORATIONS, DataComponents.POTION_CONTENTS,
                    DataComponents.TROPICAL_FISH_PATTERN, DataComponents.WRITTEN_BOOK_CONTENT,
                    DataComponents.UNBREAKABLE, DataComponents.ATTRIBUTE_MODIFIERS
	 */
	;

	private final DataComponent<?> dataComponent;

	ItemFlag(DataComponent<?> dataComponent) {
		this.dataComponent = dataComponent;
	}

	public DataComponent<?> getDataComponent() {
		return dataComponent;
	}
}
