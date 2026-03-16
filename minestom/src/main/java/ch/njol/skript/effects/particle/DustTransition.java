package ch.njol.skript.effects.particle;

import net.kyori.adventure.util.RGBLike;
import net.minestom.server.particle.Particle;

public record DustTransition(RGBLike color, RGBLike transitionColor, float scale) {

	public static Particle.DustColorTransition toParticle(Particle.DustColorTransition original, DustTransition transition) {
		return original.withProperties(transition.color, transition.transitionColor, transition.scale);
	}

}
