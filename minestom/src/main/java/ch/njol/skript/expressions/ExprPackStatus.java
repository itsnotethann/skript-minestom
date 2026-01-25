package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.sections.EffSecSendPack;
import ch.njol.util.Kleenean;
import net.kyori.adventure.resource.ResourcePackStatus;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

public class ExprPackStatus extends SimpleExpression<ResourcePackStatus> {

	static {
		Skript.registerExpression(ExprPackStatus.class, ResourcePackStatus.class, ExpressionType.SIMPLE, "[resource] pack status");
	}

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		if (!getParser().isCurrentEvent(EffSecSendPack.ResourcePackCallbackEvent.class)) {
			Skript.error("You can only use the pack status expression in the send resource pack callback section.");
			return false;
		}
		return true;
	}

	@Override
	protected @Nullable ResourcePackStatus[] get(Event event) {
		return new ResourcePackStatus[]{((EffSecSendPack.ResourcePackCallbackEvent) event).getStatus()};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends ResourcePackStatus> getReturnType() {
		return ResourcePackStatus.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "resource pack status";
	}

}
