package ch.njol.skript.util;

import net.minestom.server.entity.vehicle.PlayerInputs;
import net.minestom.server.event.player.PlayerInputEvent;

import java.util.EnumSet;
import java.util.Set;

/**
 * Enum representing different movement input keys.
 * @see PlayerInputs
 */
public enum InputKey {

	FORWARD_KEY,
	BACKWARD_KEY,
	RIGHT_KEY,
	LEFT_KEY,
	JUMP_KEY,
	SNEAK_KEY,
	SPRINT_KEY;

	/**
	 * Checks if the given {@link PlayerInputs} is pressing this {@link InputKey}.
	 *
	 * @param input the input to check
	 * @return true if the {@link PlayerInputs} is pressing this {@link InputKey}, false otherwise
	 */
	public boolean check(PlayerInputs input) {
		return switch (this) {
			case FORWARD_KEY -> input.forward();
			case BACKWARD_KEY -> input.backward();
			case RIGHT_KEY -> input.right();
			case LEFT_KEY -> input.left();
			case JUMP_KEY -> input.jump();
			case SNEAK_KEY -> input.shift();
			case SPRINT_KEY -> input.sprint();
		};
	}

	/**
	 * Returns a set of {@link InputKey}s that match the given {@link PlayerInputs}.
	 *
	 * @param input the input to check
	 * @return a set of {@link InputKey}s that match the given {@link PlayerInputs}
	 */
	public static Set<InputKey> fromInput(PlayerInputs input) {
		Set<InputKey> keys = EnumSet.noneOf(InputKey.class);
		for (InputKey key : values()) {
			if (key.check(input))
				keys.add(key);
		}
		return keys;
	}

	public static Set<InputKey> fromInput(PlayerInputEvent inputEvent) {
		boolean forward = inputEvent.wasPressingForwardKey();
		boolean backward = inputEvent.wasPressingBackwardKey();
		boolean left = inputEvent.wasPressingLeftKey();
		boolean right = inputEvent.wasPressingRightKey();
		boolean jump = inputEvent.wasPressingJumpKey();
		boolean shift = inputEvent.wasPressingShiftKey();
		boolean sprint = inputEvent.wasPressingSprintKey();
		PlayerInputs playerInputs = new PlayerInputs();
		playerInputs.refresh(forward, backward, left, right, jump, shift, sprint);
		return fromInput(playerInputs);
	}

}
