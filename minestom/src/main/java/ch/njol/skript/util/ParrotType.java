package ch.njol.skript.util;

public enum ParrotType {

	RED_PARROT(0),
	BLUE_PARROT(1),
	GREEN_PARROT(2),
	LIGHT_BLUE_PARROT(3),
	GRAY_PARROT(4),
	GREY_PARROT(4);

	private final int internalDataValue;

	ParrotType(int internalDataValue) {
		this.internalDataValue = internalDataValue;
	}

	public int getInternalDataValue() {
		return internalDataValue;
	}

	public static ParrotType of(int internalDataValue) {
		for (ParrotType type : values()) {
			if (type.internalDataValue == internalDataValue) return type;
		}
		return null;
	}

}
