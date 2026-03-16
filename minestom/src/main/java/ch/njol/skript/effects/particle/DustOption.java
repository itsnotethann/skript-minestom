package ch.njol.skript.effects.particle;

import net.kyori.adventure.util.RGBLike;
import net.minestom.server.particle.Particle;

public record DustOption(RGBLike color, float scale) {

	public static Particle.Dust toParticle(Particle.Dust original, DustOption opt) {
		return original.withProperties(opt.color, opt.scale);
	}

}
