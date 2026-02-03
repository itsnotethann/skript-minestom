package ch.njol.skript.effects.particle;

import net.minestom.server.coordinate.Point;
import net.minestom.server.particle.Particle;

public class VibrationData {

	private final Particle.Vibration.SourceType sourceType;
	private final Point sourceBlock;
	private final int sourceEntityId;
	private final float sourceEntityEyeHeight;
	private final int travelTicks;

	public VibrationData(Particle.Vibration.SourceType sourceType, Point sourceBlock, int sourceEntityId, float sourceEntityEyeHeight,
						 int travelTicks) {
		this.sourceType = sourceType;
		this.sourceBlock = sourceBlock;
		this.sourceEntityId = sourceEntityId;
		this.sourceEntityEyeHeight = sourceEntityEyeHeight;
		this.travelTicks = travelTicks;
	}

	public Particle.Vibration.SourceType getSourceType() {
		return sourceType;
	}

	public Point getSourceBlock() {
		return sourceBlock;
	}

	public int getSourceEntityId() {
		return sourceEntityId;
	}

	public float getSourceEntityEyeHeight() {
		return sourceEntityEyeHeight;
	}

	public int getTravelTicks() {
		return travelTicks;
	}

	public static Particle.Vibration toParticle(Particle.Vibration original, VibrationData data) {
		return original.withProperties(data.sourceType, data.sourceBlock, data.sourceEntityId, data.sourceEntityEyeHeight,
			data.travelTicks);
	}

}
