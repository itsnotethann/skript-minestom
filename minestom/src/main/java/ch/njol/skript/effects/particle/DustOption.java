package ch.njol.skript.effects.particle;

import net.kyori.adventure.util.RGBLike;
import net.minestom.server.particle.Particle;

public class DustOption {

	private final RGBLike color;
	private final float scale;

	public DustOption(RGBLike color, float scale) {
		this.color = color;
		this.scale = scale;
	}

	public RGBLike getColor() {
		return color;
	}

	public float getScale() {
		return scale;
	}

	public static Particle.Dust toParticle(Particle.Dust original, DustOption opt) {
		return original.withProperties(opt.color, opt.scale);
	}

}
