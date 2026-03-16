package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.registry.DynamicRegistry;
import net.minestom.server.registry.RegistryKey;
import net.minestom.server.world.biome.Biome;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ExprBiome extends SimpleExpression<Biome> {

	static {
		Skript.registerExpression(ExprBiome.class, Biome.class, ExpressionType.SIMPLE,
			"biome[s] %directions% %points% [in [(world|instance)] %instances%]");
	}

	private Expression<? extends Point> pointExpr;
	private Expression<Instance> instanceExpr;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		pointExpr = Direction.combine((Expression<? extends Direction>) expressions[0], (Expression<? extends Point>) expressions[1]);
		instanceExpr = (Expression<Instance>) expressions[2];
		return true;
	}

	@Override
	protected @Nullable Biome[] get(Event event) {
		Instance[] instances = instanceExpr.getArray(event);
		Point[] points = pointExpr.getArray(event);
		List<Biome> biomes = new ArrayList<>();
		DynamicRegistry<Biome> biomeRegistry = MinecraftServer.getBiomeRegistry();
		for (Instance instance : instances) {
			for (Point point : points) {
				RegistryKey<Biome> biome = instance.getBiome(point);
				Biome value = biomeRegistry.get(biome);
				if (value == null) continue;
				biomes.add(value);
			}
		}
		return biomes.toArray(new Biome[0]);
	}

	@SuppressWarnings("DataFlowIssue")
	@Override
	@Nullable
	public Class<?>[] acceptChange(Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET) return CollectionUtils.array(Biome.class);
		return null;
	}

	@Override
	public void change(Event event, @Nullable Object[] delta, Changer.ChangeMode mode) throws UnsupportedOperationException {
		if (delta[0] == null) return;
		Biome biome = (Biome) delta[0];
		RegistryKey<Biome> key = MinecraftServer.getBiomeRegistry().getKey(biome);
		if (key == null) return;
		Instance instance = instanceExpr.getSingle(event);
		if (instance == null) return;
		Point[] points = pointExpr.getArray(event);
		for (Point p : points) {
			if (!instance.isChunkLoaded(p)) continue;
			instance.setBiome(p, key);
		}
	}

	@Override
	public boolean isSingle() {
		return pointExpr.isSingle();
	}

	@Override
	public Class<? extends Biome> getReturnType() {
		return Biome.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "biome " + pointExpr.toString(event, debug) + " in instance " + instanceExpr.toString(event, debug);
	}

}

