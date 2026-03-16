package ch.njol.skript.effects.particle;

import net.kyori.adventure.util.RGBLike;
import net.minestom.server.coordinate.Point;
import net.minestom.server.particle.Particle;

public record TrailData(Point target, RGBLike color, int duration) {

	public static Particle.Trail toParticle(Particle.Trail original, TrailData opt) {
		return original.withProperties(opt.target, opt.color, opt.duration);
	}

}
