package ch.njol.skript.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.events.wrapper.EntitySpawnWrapper;
import ch.njol.skript.lang.*;
import ch.njol.skript.util.Direction;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.*;
import net.minestom.server.event.entity.EntitySpawnEvent;
import net.minestom.server.instance.Instance;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import java.util.List;

public class EffSecSpawn extends EffectSection {

	static {
		Skript.registerSection(EffSecSpawn.class,
			"(summon|spawn) [(:navigable|:living)] %entitytypes% [%directions% %points%] [in [(world|instance)[s]] %instances%]",
			"(summon|spawn) %integer% [of] [(:navigable|:living)] %entitytypes% [%directions% %points%] [in [(world|instance)[s]] %instances%]");
	}

	private Expression<Number> amount;
	private Expression<EntityType> types;
	private Expression<Point> points;
	private Expression<Instance> instances;
	@Nullable
	private String type;
	@Nullable
	private Trigger spawnTrigger;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult,
						@Nullable SectionNode sectionNode, @Nullable List<TriggerItem> triggerItems) {
		if (matchedPattern == 1) amount = (Expression<Number>) expressions[0];
		types = (Expression<EntityType>) expressions[matchedPattern];
		points = Direction.combine((Expression<? extends Direction>) expressions[1+matchedPattern], (Expression<? extends Point>) expressions[2+matchedPattern]);
		instances = (Expression<Instance>) expressions[3+matchedPattern];
		if (!parseResult.tags.isEmpty()) type = parseResult.tags.getFirst();
		if (sectionNode != null) spawnTrigger = loadCode(sectionNode, "spawn trigger", EntitySpawnWrapper.class);
		return true;
	}

	@Override
	protected @Nullable TriggerItem walk(Event event) {
		Integer amount = null;
		if (this.amount != null) {
			Number num = this.amount.getSingle(event);
			if (num != null) amount = num.intValue();
		}
		if (amount == null) amount = 1;
		EntityType[] types = this.types.getArray(event);
		Point[] points = this.points.getArray(event);
		Instance[] instances = this.instances.getArray(event);
		Object variables = Variables.copyLocalVariables(event);
		Object mostRecentLocals = variables;
		for (EntityType type : types) {
			if (type == EntityType.PLAYER) continue;
			for (Instance instance : instances) {
				for (Point point : points) {
					for (int i = 0; i < amount; i++) {
						Entity entity = switch (this.type) {
							case "navigable" -> new EntityCreature(type);
							case "living" -> new LivingEntity(type);
							case null, default -> new Entity(type);
						};
						if (spawnTrigger != null) {
							Event e = new EntitySpawnWrapper(new EntitySpawnEvent(entity, instance));
							Variables.setLocalVariables(e, variables);
							TriggerItem.walk(spawnTrigger, e);
							mostRecentLocals = Variables.copyLocalVariables(e);
						}
						entity.setInstance(instance, point);
					}
				}
			}
		}
		Variables.setLocalVariables(event, mostRecentLocals);
		return super.walk(event, false);
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		String type = this.type == null ? "normal" : this.type;
		String amount = this.amount == null ? "1" : this.amount.toString(event, debug);
		return "spawn " + amount + " of " + type + " " + types.toString(event, debug) + " "
			+ points.toString(event, debug) + " in instances " + instances.toString(event, debug);
	}

}
