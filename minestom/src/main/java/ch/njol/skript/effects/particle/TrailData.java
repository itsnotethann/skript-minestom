package ch.njol.skript.effects.particle;

import net.kyori.adventure.util.RGBLike;
import net.minestom.server.coordinate.Point;
import net.minestom.server.particle.Particle;

public class TrailData {

	private final Point target;
	private final RGBLike color;
	private final int duration;

	public TrailData(Point target, RGBLike color, int duration) {
		this.target = target;
		this.color = color;
		this.duration = duration;
	}

	public Point getTarget() {
		return target;
	}

	public RGBLike getColor() {
		return color;
	}

	public int getDuration() {
		return duration;
	}

	public static Particle.Trail toParticle(Particle.Trail original, TrailData opt) {
		return original.withProperties(opt.target, opt.color, opt.duration);
	}

}
