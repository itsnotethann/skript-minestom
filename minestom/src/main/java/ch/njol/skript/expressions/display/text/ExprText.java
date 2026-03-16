package ch.njol.skript.expressions.display.text;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import org.bukkit.event.Event;

@Name("Display Text")
@Description("The text displayed by a text display entity.")
@Examples("set display text of targeted entity to \"Hello World!\"")
public class ExprText extends SimplePropertyExpression<Entity, Component> {

	static {
		register(ExprText.class, Component.class, "[display] text", "entities");
	}

	@Override
	public Component convert(Entity from) {
		if (!(from.getEntityMeta() instanceof TextDisplayMeta meta)) return null;
		return meta.getText();
	}

	@Override
	public @org.eclipse.jdt.annotation.Nullable Class<?>[] acceptChange(Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) return CollectionUtils.array(Component.class);
		return null;
	}

	@SuppressWarnings("ConstantValue")
	@Override
	public void change(Event event, @org.jspecify.annotations.Nullable @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) {
		Component text = delta == null ? null : (Component) delta[0];
		for (Entity entity : getExpr().getArray(event)) {
			if (!(entity.getEntityMeta() instanceof TextDisplayMeta meta)) continue;
			switch (mode) {
				case SET -> {
					if (text == null) return;
					meta.setText(text);
				}
				case RESET -> meta.setText(Component.empty());
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "display text";
	}

	@Override
	public Class<? extends Component> getReturnType() {
		return Component.class;
	}

}