package ch.njol.skript.effects.particle;

import net.kyori.adventure.util.RGBLike;
import net.minestom.server.particle.Particle;

public class EffectData {

	private final RGBLike color;
	private final float power;

	public EffectData(RGBLike color, float power) {
		this.color = color;
		this.power = power;
	}

	public RGBLike getColor() {
		return color;
	}

	public float getPower() {
		return power;
	}

	public static Particle.Effect toParticle(Particle.Effect original, EffectData opt) {
		return original.withProperties(opt.color, opt.power);
	}

	public static Particle.InstantEffect toParticle(Particle.InstantEffect original, EffectData opt) {
		return original.withProperties(opt.color, opt.power);
	}

}
