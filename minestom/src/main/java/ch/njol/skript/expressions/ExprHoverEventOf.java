package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.ComponentWrapper;
import ch.njol.skript.util.Item;
import ch.njol.util.Kleenean;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.minestom.server.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("rawtypes")

@Name("Hover Event Of")
@Description("Creates a hover event that shows a component, item, or entity.")
@Examples("set {_hover} to hover event showing player")
public class ExprHoverEventOf extends SimpleExpression<HoverEvent> {

	static {
		Skript.registerExpression(ExprHoverEventOf.class, HoverEvent.class, ExpressionType.PROPERTY,
			"hover event (show|display)ing %components/items/entities%");
	}

	private Expression<Object> objects;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		objects = (Expression<Object>) expressions[0];
		return true;
	}

	@Override
	protected HoverEvent @Nullable [] get(Event event) {
		Object[] objects = this.objects.getArray(event);
		HoverEvent[] hoverEvents = new HoverEvent[objects.length];
		for (int i = 0; i < objects.length; i++) {
			hoverEvents[i] = getHoverEvent(objects[i]);
		}
		return hoverEvents;
	}

	@Override
	public boolean isSingle() {
		return objects.isSingle();
	}

	@Override
	public Class<? extends HoverEvent> getReturnType() {
		return HoverEvent.class;
	}

	@Override
	public String toString(@org.eclipse.jdt.annotation.Nullable Event event, boolean debug) {
		return "hover event displaying " + objects.toString(event, debug);
	}

	private HoverEvent getHoverEvent(Object o) {
		return switch (o) {
			case ComponentWrapper c -> c.getComponent().asHoverEvent();
			case Item i -> i.getItem().asHoverEvent();
			default -> ((Entity) o).asHoverEvent();
		};
	}

}
