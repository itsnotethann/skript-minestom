package ch.njol.skript.effects.particle;

import net.minestom.server.coordinate.Point;
import net.minestom.server.particle.Particle;

public record VibrationData(Particle.Vibration.SourceType sourceType, Point sourceBlock, int sourceEntityId,
							float sourceEntityEyeHeight, int travelTicks) {

	public static Particle.Vibration toParticle(Particle.Vibration original, VibrationData data) {
		return original.withProperties(data.sourceType, data.sourceBlock, data.sourceEntityId, data.sourceEntityEyeHeight,
			data.travelTicks);
	}

}
