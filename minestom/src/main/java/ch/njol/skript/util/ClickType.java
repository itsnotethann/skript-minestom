package ch.njol.skript.util;

import net.minestom.server.inventory.click.Click;

import java.util.function.Function;

public enum ClickType {

	LEFT_DROP_ITEM(Click.LeftDropCursor.class),
	MIDDLE_DROP_ITEM(Click.MiddleDropCursor.class),
	RIGHT_DROP_ITEM(Click.RightDropCursor.class),
	LEFT_DRAG(Click.LeftDrag.class),
	MIDDLE_DRAG(Click.MiddleDrag.class),
	RIGHT_DRAG(Click.RightDrag.class),
	LEFT_CLICK(Click.Left.class),
	MIDDLE_CLICK(Click.Middle.class),
	RIGHT_CLICK(Click.Right.class),
	SHIFT_LEFT_CLICK(Click.LeftShift.class),
	SHIFT_RIGHT_CLICK(Click.RightShift.class),
	DOUBLE_CLICK(Click.Double.class),
	DROP_ALL_ITEMS(Click.DropSlot.class, Click.DropSlot::all),
	DROP_ITEMS(Click.DropSlot.class, drop -> !drop.all()),
	HOTBAR_ITEM_SWAP(Click.HotbarSwap.class),
	OFFHAND_SWAP(Click.OffhandSwap.class);

	private final Class<? extends Click> clickClass;
	private final Function<Click, Boolean> comparator;

	ClickType(Class<? extends Click> clickClass) {
		this.clickClass = clickClass;
		this.comparator = click -> clickClass.isAssignableFrom(click.getClass());
	}

	@SuppressWarnings("unchecked")
	<T extends Click> ClickType(Class<T> clickClass, Function<T, Boolean> comparator) {
		this.clickClass = clickClass;
		this.comparator = click -> {
			if (!clickClass.isAssignableFrom(click.getClass())) return false;
			return comparator.apply((T) click);
		};
	}

	public static ClickType getType(Click click) {
		for (ClickType type : values()) {
			if (type.comparator.apply(click)) return type;
		}
		return null;
	}

}
