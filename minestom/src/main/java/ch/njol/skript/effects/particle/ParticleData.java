package ch.njol.skript.effects.particle;

import net.minestom.server.particle.Particle;

public interface ParticleData<T extends Particle> {

	T toParticle(T original);

}
