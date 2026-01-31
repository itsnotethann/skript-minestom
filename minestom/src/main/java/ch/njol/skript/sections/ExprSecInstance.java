package ch.njol.skript.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.effects.EffChange;
import ch.njol.skript.events.wrapper.EntitySpawnWrapper;
import ch.njol.skript.expressions.base.SectionExpression;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.parser.ParsingStack;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.entity.Entity;
import net.minestom.server.event.entity.EntitySpawnEvent;
import net.minestom.server.instance.Instance;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ExprSecInstance extends SectionExpression<Instance> {

	static {
		Skript.registerExpression(ExprSecInstance.class, Instance.class, ExpressionType.PROPERTY,
			"(instance|world) of %entities%",
			"%entities%'[s] (instance|world)");
	}

	private Expression<Entity> entities;

	private Trigger runWhenComplete;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int pattern, Kleenean delayed, SkriptParser.ParseResult result, @Nullable SectionNode node, @Nullable List<TriggerItem> triggerItems) {
		entities = (Expression<Entity>) expressions[0];
		if (inEffChange(this) && node != null) runWhenComplete = loadCode(node, "instance set callback",
			EntitySpawnWrapper.class);
		return true;
	}

	@Override
	protected @org.jspecify.annotations.Nullable Instance[] get(Event event) {
		Entity[] entities = this.entities.getArray(event);
		Instance[] instances = new Instance[entities.length];
		for (int i = 0; i < entities.length; i++) {
			instances[i] = entities[i].getInstance();
		}
		return instances;
	}

	@Override
	public @org.eclipse.jdt.annotation.Nullable Class<?>[] acceptChange(Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET) return CollectionUtils.array(Instance.class);
		return null;
	}

	@Override
	public void change(Event event, @org.jspecify.annotations.Nullable @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) {
		Instance instance = delta == null ? null : (Instance) delta[0];
		if (instance == null) return;
		Object variables = Variables.copyLocalVariables(event);
		for (Entity e : entities.getArray(event)) {
			if (e.getInstance().equals(instance)) continue;
			if (runWhenComplete == null) {
				System.out.println("running setInstance on " + Thread.currentThread());
				System.out.println(e.acquirable().assignedThread());
				System.out.println("actually running on " + Thread.currentThread());
				e.setInstance(instance);
				System.out.println("after");
				//e.setInstance(instance);
				//System.out.println("after");
				//e.acquirable().sync(entity -> entity.setInstance(instance));
			}
			else e.setInstance(instance).whenComplete((unused, throwable) -> {
				if (throwable != null) return;
				Event spawnEvent = new EntitySpawnWrapper(new EntitySpawnEvent(e, instance));
				//Variables.withLocalVariables(variables, spawnEvent, () -> TriggerItem.walk(runWhenComplete, spawnEvent));
			});
		}
	}

	@Override
	public boolean isSingle() {
		return entities.isSingle();
	}

	@Override
	public Class<? extends Instance> getReturnType() {
		return Instance.class;
	}

	@Override
	public String toString(@org.eclipse.jdt.annotation.Nullable Event event, boolean debug) {
		return "instance of " + entities.toString(event, debug);
	}

	// todo maybe change this to only check 1st ParsingStack element
	public static boolean inEffChange(SyntaxElement syntaxElement) {
		for (ParsingStack.Element element : syntaxElement.getParser().getParsingStack()) {
			if (!EffChange.class.isAssignableFrom(element.getSyntaxElementClass())) continue;
			return true;
		}
		return false;
	}

}
