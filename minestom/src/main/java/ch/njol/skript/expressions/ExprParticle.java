package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.effects.particle.*;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.Item;
import ch.njol.util.Kleenean;
import net.minestom.server.color.AlphaColor;
import net.minestom.server.instance.block.Block;
import net.minestom.server.particle.Particle;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.converter.Converters;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;


@Name("Particle With Data")
@Description("A particle with extra data such as block, item, or dust options.")
@Examples("set {_p} to block particle using stone")
public class ExprParticle extends SimpleExpression<Particle> {

	public static final Map<Class<? extends Particle>, ParticleRegistryInfo> PARTICLE_REGISTRY = new HashMap<>();

	static {
		register(Particle.Block.class, Block.class, Particle.Block::withBlock);
		register(Particle.BlockMarker.class, Block.class, Particle.BlockMarker::withBlock);
		register(Particle.Dust.class, DustOption.class, DustOption::toParticle);
		register(Particle.DustColorTransition.class, DustTransition.class, DustTransition::toParticle);
		register(Particle.DustPillar.class, Block.class, Particle.DustPillar::withBlock);
		register(Particle.FallingDust.class, Block.class, Particle.FallingDust::withBlock);
		register(Particle.Item.class, Item.class, (item, item2) -> item.withItem(item2.getItem()));
		register(Particle.EntityEffect.class, AlphaColor.class, Particle.EntityEffect::withColor);
		register(Particle.SculkCharge.class, Number.class, (sculkCharge, number) -> sculkCharge.withRoll(number.floatValue()));
		register(Particle.Shriek.class, Integer.class, Particle.Shriek::withDelay);
		register(Particle.Vibration.class, VibrationData.class, VibrationData::toParticle);
		register(Particle.Trail.class, TrailData.class, TrailData::toParticle);
		register(Particle.BlockCrumble.class, Block.class, Particle.BlockCrumble::withBlock);
		register(Particle.TintedLeaves.class, AlphaColor.class, Particle.TintedLeaves::withColor);
		register(Particle.DragonBreath.class, Number.class, (dragonBreath, number) -> dragonBreath.withPower(number.floatValue()));
		register(Particle.Effect.class, EffectData.class, EffectData::toParticle);
		register(Particle.Flash.class, AlphaColor.class, Particle.Flash::withColor);
		register(Particle.InstantEffect.class, EffectData.class, EffectData::toParticle);

		Skript.registerExpression(ExprParticle.class, Particle.class, ExpressionType.COMBINED,
			"%*particle% (using|with) %item/dustoption/dusttransition/block/rgblike/number/integer/vibrationdata/traildata/effectdata%");
	}

	private Particle particle;
	private Expression<?> using;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		particle = ((Literal<Particle>) expressions[0]).getSingle();
		using = expressions[1];
		Class<? extends Particle> particleClass = particle.getClass();
		String particleName = Classes.toString(particle);
		if (!PARTICLE_REGISTRY.containsKey(particleClass)) {
			Skript.error("Particle '" + particleName + "' doesn't accept extra particle data (e.g. dust option).");
			return false;
		}
		Class<?> usingClass = using.getReturnType();
		Class<?> expectedExtraClass = PARTICLE_REGISTRY.get(particleClass).expectedExtra;
		if (!expectedExtraClass.isAssignableFrom(usingClass)) {
			if (!Converters.converterExists(usingClass, expectedExtraClass)) {
				String type = Classes.getSuperClassInfo(usingClass).toString();
				Skript.error("Particle '" + particleName + "' doesn't accept particle data of type '" + type + "'.");
				return false;
			}
			using = using.getConvertedExpression(expectedExtraClass);
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Particle @Nullable [] get(Event event) {
		Class<? extends Particle> particleClass = particle.getClass();
		Object using = this.using == null ? null : this.using.getSingle(event);
		if (PARTICLE_REGISTRY.containsKey(particleClass) && using != null) {
			BiFunction<Particle, Object, Particle> function = (BiFunction<Particle, Object, Particle>) PARTICLE_REGISTRY.get(particleClass).function();
			particle = function.apply(particle, using);
		}
		return new Particle[]{particle};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends Particle> getReturnType() {
		return Particle.class;
	}

	@Override
	public String toString(@org.eclipse.jdt.annotation.Nullable Event event, boolean debug) {
		return Classes.toString(particle) + " using " + using.toString(event, debug);
	}

	private static <T, P extends Particle> void register(Class<P> type, Class<T> expectedExtra, BiFunction<P, T, P> func) {
		PARTICLE_REGISTRY.put(type, new ParticleRegistryInfo(expectedExtra, func));
	}

	public record ParticleRegistryInfo(Class<?> expectedExtra, BiFunction<?, ?, ?> function) {}

}
