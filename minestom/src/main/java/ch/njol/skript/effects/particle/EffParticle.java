package ch.njol.skript.effects.particle;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.*;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.Direction;
import ch.njol.skript.util.Item;
import ch.njol.util.Kleenean;
import net.minestom.server.color.AlphaColor;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import java.util.*;
import java.util.function.BiFunction;

public class EffParticle extends Effect {

	private static final Map<Class<? extends Particle>, ParticleRegistryInfo> PARTICLE_REGISTRY = new HashMap<>();

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

		Skript.registerEffect(EffParticle.class,
			"[:force] draw %integer% [of] %particle% "
				+ "[(using|with) %-block/dustoption/dusttransition/item/color/number/integer/vibrationdata/traildata/effectdata%]"
				+ " [(with offset|offset by) %-vector%] [%directions% %points%] [in [(world|instance)] %-instances%] "
				+ "[(to|for) %-players%] [with (speed|extra) %-number%] [without (:distance) limit[s]]");
	}

	private boolean force = false;

	private Expression<Integer> amount;
	private Particle particle;
	@Nullable
	private Expression<Object> using;
	@Nullable
	private Expression<Vec> offset;
	private Expression<Point> points;
	@Nullable
	private Expression<Instance> instances;
	@Nullable
	private Expression<Player> players;
	@Nullable
	private Expression<Number> extra;
	private boolean longDistance = false;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		force = parseResult.hasTag("force");
		amount = (Expression<Integer>) expressions[0];
		particle = ((Literal<Particle>) expressions[1]).getSingle();
		using = (Expression<Object>) expressions[2];

		Class<? extends Particle> particleClass = particle.getClass();
		String particleName = Classes.toString(particle);
		if (using != null) {
			if (!PARTICLE_REGISTRY.containsKey(particleClass)) {
				Skript.error("Particle '" + particleName + "' doesn't accept extra particle data (e.g. dust option).");
				return false;
			}
			/*Class<?> usingClass = using.getReturnType();
			if (!PARTICLE_REGISTRY.get(particleClass).expectedExtra.equals(usingClass)) {
				String providedUsingType = Classes.getExactClassName(usingClass);
				Skript.error("Particle '" + particleName + "' doesn't accept particle data of type '" + providedUsingType + "'.");
				return false;
			}*/
		} else if (PARTICLE_REGISTRY.containsKey(particleClass)) {
			String requiredUsingType = Classes.getExactClassName(PARTICLE_REGISTRY.get(particleClass).expectedExtra);
			Skript.error("Particle '" + particleName + "' requires extra data of type '" + requiredUsingType + "', but none was provided.");
			return false;
		}

		offset = (Expression<Vec>) expressions[3];
		points = Direction.combine((Expression<? extends Direction>) expressions[4], (Expression<? extends Point>) expressions[5]);
		instances = (Expression<Instance>) expressions[6];
		players = (Expression<Player>) expressions[7];

		if (instances == null && players == null) {
			Skript.error("Instances and players cannot be null, provide one or both.");
			return false;
		}

		extra = (Expression<Number>) expressions[8];
		longDistance = parseResult.hasTag("distance");
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void execute(Event event) {
		Particle particle = this.particle;
		Class<? extends Particle> particleClass = particle.getClass();
		Object using = this.using == null ? null : this.using.getSingle(event);
		if (PARTICLE_REGISTRY.containsKey(particleClass) && using != null) {
			BiFunction<Particle, Object, Particle> function = (BiFunction<Particle, Object, Particle>) PARTICLE_REGISTRY.get(particleClass).function;
			particle = function.apply(particle, using);
		}
		Vec offset = this.offset == null ? Vec.ZERO : this.offset.getSingle(event);
		if (offset == null) offset = Vec.ZERO;
		float extra = 1f;
		if (this.extra != null) {
			Number num = this.extra.getSingle(event);
			if (num != null) extra = num.floatValue();
		}
		Integer amount = this.amount.getSingle(event);
		if (amount == null) amount = 0;
		List<Player> players = collectPlayers(event);
		for (Point point : points.getArray(event)) {
			ParticlePacket packet = new ParticlePacket(particle, force, longDistance, point, offset, extra, amount);
			for (Player player : players) {
				player.sendPacket(packet);
			}
		}
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		SyntaxStringBuilder syntaxBuilder = new SyntaxStringBuilder(event, debug);
		if (force) syntaxBuilder.append("force");
		syntaxBuilder.append("draw", amount, "of", Classes.toString(particle));
		if (using != null) syntaxBuilder.append("using", using);
		if (offset != null) syntaxBuilder.append("with offset", offset);
		syntaxBuilder.append(points);
		if (instances != null) syntaxBuilder.append("in instances", instances);
		if (players != null) syntaxBuilder.append("for", players);
		if (extra != null) syntaxBuilder.append("with extra", extra);
		if (longDistance) syntaxBuilder.append("without distance limit");
		return syntaxBuilder.toString();
	}

	private List<Player> collectPlayers(Event event) {
		List<Player> players = new ArrayList<>();
		if (this.players != null) players.addAll(List.of(this.players.getArray(event)));
		if (instances != null) {
			Set<Instance> instances = new HashSet<>(List.of(this.instances.getArray(event)));
			if (this.players != null) {
				for (Player player : List.copyOf(players)) {
					if (!instances.contains(player.getInstance())) players.remove(player);
				}
			} else {
				for (Instance instance : instances) {
					players.addAll(instance.getPlayers());
				}
			}
		}
		return players;
	}

	private static <T, P extends Particle> void register(Class<P> type, Class<T> expectedExtra, BiFunction<P, T, P> func) {
		PARTICLE_REGISTRY.put(type, new ParticleRegistryInfo(expectedExtra, func));
	}

	record ParticleRegistryInfo(Class<?> expectedExtra, BiFunction<?, ?, ?> function) {}


}
