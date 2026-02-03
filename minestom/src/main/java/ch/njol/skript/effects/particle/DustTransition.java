package ch.njol.skript.effects.particle;

import net.kyori.adventure.util.RGBLike;
import net.minestom.server.particle.Particle;

public class DustTransition {

	private final RGBLike color;
	private final RGBLike transitionColor;
	private final float scale;

	public DustTransition(RGBLike color, RGBLike transitionColor, float scale) {
		this.color = color;
		this.transitionColor = transitionColor;
		this.scale = scale;
	}

	public RGBLike getColor() {
		return color;
	}

	public RGBLike getTransitionColor() {
		return transitionColor;
	}

	public float getScale() {
		return scale;
	}

	public static Particle.DustColorTransition toParticle(Particle.DustColorTransition original, DustTransition transition) {
		return original.withProperties(transition.color, transition.transitionColor, transition.scale);
	}

}
