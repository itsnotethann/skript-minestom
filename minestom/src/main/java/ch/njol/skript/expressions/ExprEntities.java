package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.instance.Instance;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Name("Entities")
@Description("Returns all entities on the server, optionally filtered by type, radius, or instance.")
@Examples("set {_entities::*} to all zombies in radius 10 of player")
public class ExprEntities extends SimpleExpression<Entity> {

	static {
		Skript.registerExpression(ExprEntities.class, Entity.class, ExpressionType.COMBINED,
			"all entities [of type[s] %-entitytypes%] [in radius %-number% of %-points%] [(in|of|from) [instance[s]] %-instances%]",
			"all %entitytypes% [in radius %-number% of %-points%] [(in|of|from) [instance[s]] %-instances%]");
	}

	@org.eclipse.jdt.annotation.Nullable
	private Expression<EntityType> types;
	@org.eclipse.jdt.annotation.Nullable
	private Expression<Number> radius;
	@org.eclipse.jdt.annotation.Nullable
	private Expression<Point> points;
	@org.eclipse.jdt.annotation.Nullable
	private Expression<Instance> instances;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		types = (Expression<EntityType>) expressions[0];
		radius = (Expression<Number>) expressions[1];
		points = (Expression<Point>) expressions[2];
		instances = (Expression<Instance>) expressions[3];
		return true;
	}

	@Override
	protected @Nullable Entity[] get(Event event) {
		Collection<Instance> instances = this.instances == null ? MinecraftServer.getInstanceManager().getInstances() : List.of(this.instances.getArray(event));
		Collection<EntityType> types = this.types == null ? null : List.of(this.types.getArray(event));
		Double radius = this.radius == null ? null : (this.radius.getSingle(event) == null ? null : this.radius.getSingle(event).doubleValue());
		Collection<Point> points = this.points == null ? null : List.of(this.points.getArray(event));
		List<Entity> entities = new ArrayList<>();
		for (Instance instance : instances) {
			for (Entity e : instance.getEntities()) {
				if (types != null && !types.contains(e.getEntityType())) continue;
				Pos entityPos = e.getPosition();
				if (radius != null && points != null) {
					for (Point point : points) {
						if (entityPos.distance(point) <= radius) entities.add(e);
					}
				} else entities.add(e);
			}
		}
		return entities.toArray(new Entity[0]);
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@Override
	public Class<? extends Entity> getReturnType() {
		return Entity.class;
	}

	@Override
	public String toString(@org.eclipse.jdt.annotation.Nullable Event event, boolean debug) {
		SyntaxStringBuilder builder = new SyntaxStringBuilder(event, debug);
		builder.append("all");
		if (types != null) builder.append("of the entities of type", types);
		else builder.append("entities");
		if (radius != null && points != null) builder.append("in radius", radius, "of", points);
		if (instances != null) builder.append("in instances", instances);
		return builder.toString();
	}

}
