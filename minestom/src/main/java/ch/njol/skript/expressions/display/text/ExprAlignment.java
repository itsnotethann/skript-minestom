package ch.njol.skript.expressions.display.text;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import org.bukkit.event.Event;

@Name("Text Alignment")
@Description("The alignment of a text display entity.")
@Examples("set alignment of {_entity} to center")
public class ExprAlignment extends SimplePropertyExpression<Entity, TextDisplayMeta.Alignment> {

	static {
		register(ExprAlignment.class, TextDisplayMeta.Alignment.class, "text alignment", "entities");
	}

	@Override
	public TextDisplayMeta.Alignment convert(Entity from) {
		if (!(from.getEntityMeta() instanceof TextDisplayMeta meta)) return null;
		return meta.getAlignment();
	}

	@Override
	public @org.eclipse.jdt.annotation.Nullable Class<?>[] acceptChange(Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) return CollectionUtils.array(TextDisplayMeta.Alignment.class);
		return null;
	}

	@SuppressWarnings("ConstantValue")
	@Override
	public void change(Event event, @org.jspecify.annotations.Nullable @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) {
		TextDisplayMeta.Alignment alignment = delta == null ? null : (TextDisplayMeta.Alignment) delta[0];
		for (Entity entity : getExpr().getArray(event)) {
			if (!(entity.getEntityMeta() instanceof TextDisplayMeta meta)) continue;
			switch (mode) {
				case SET -> {
					if (alignment == null) return;
					meta.setAlignment(alignment);
				}
				case RESET -> meta.setAlignment(TextDisplayMeta.Alignment.CENTER);
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "text alignment";
	}

	@Override
	public Class<? extends TextDisplayMeta.Alignment> getReturnType() {
		return TextDisplayMeta.Alignment.class;
	}

}
