package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.InstanceContainer;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;

import static ch.njol.skript.sections.EffSecCreateInstance.RELIGHT_INSTANCES;

public class ExprInstanceCopy extends SimpleExpression<InstanceContainer> {

	static {
		Skript.registerExpression(ExprInstanceCopy.class, InstanceContainer.class, ExpressionType.COMBINED,
			"instance[[ ]container] cop(y|ies) of %instancecontainers%");
	}

	private Expression<InstanceContainer> originalContainers;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		originalContainers = (Expression<InstanceContainer>) expressions[0];
		return true;
	}

	@Override
	protected @Nullable InstanceContainer[] get(Event event) {
		InstanceContainer[] containers = originalContainers.getArray(event);
		for (int i = 0; i < containers.length; i++) {
			InstanceContainer original = containers[i];
			InstanceContainer newContainer = original.copy();
			MinecraftServer.getInstanceManager().registerInstance(newContainer);
			if (RELIGHT_INSTANCES.contains(original)) RELIGHT_INSTANCES.add(newContainer);
			containers[i] = newContainer;
		}
		return containers;
	}

	@Override
	public boolean isSingle() {
		return originalContainers.isSingle();
	}

	@Override
	public Class<? extends InstanceContainer> getReturnType() {
		return InstanceContainer.class;
	}

	@Override
	public String toString(@org.eclipse.jdt.annotation.Nullable Event event, boolean debug) {
		return "instance copy of " + originalContainers.toString(event, debug);
	}

}
