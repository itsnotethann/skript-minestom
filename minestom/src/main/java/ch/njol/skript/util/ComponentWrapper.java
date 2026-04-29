package ch.njol.skript.util;

import ch.njol.skript.lang.Expression;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

public class ComponentWrapper {

	private final AtomicReference<Component> component;

	public ComponentWrapper(Component component) {
		this.component = new AtomicReference<>(component);
	}

	public void modify(UnaryOperator<Component> modifyFunction) {
		component.updateAndGet(modifyFunction);
	}

	public Component getComponent() {
		return component.get();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		ComponentWrapper that = (ComponentWrapper) o;
		return Objects.equals(getComponent(), that.getComponent());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getComponent());
	}

	public static Component getOrElse(@Nullable Expression<ComponentWrapper> expr, Event event, Component other) {
		if (expr != null) {
			ComponentWrapper wrapper = expr.getSingle(event);
			if (wrapper != null) other = wrapper.getComponent();
		}
		return other;
	}

	public static ComponentWrapper empty() {
		return new ComponentWrapper(Component.empty());
	}

	public static ComponentWrapper toWrapper(Component component) {
		if (component == null) return null;
		return new ComponentWrapper(component);
	}

}
