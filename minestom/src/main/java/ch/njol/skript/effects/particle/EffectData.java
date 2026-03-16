package ch.njol.skript.effects.particle;

import net.kyori.adventure.util.RGBLike;
import net.minestom.server.particle.Particle;

public record EffectData(RGBLike color, float power) {

	public static Particle.Effect toParticle(Particle.Effect original, EffectData opt) {
		return original.withProperties(opt.color, opt.power);
	}

	public static Particle.InstantEffect toParticle(Particle.InstantEffect original, EffectData opt) {
		return original.withProperties(opt.color, opt.power);
	}

}
