package ch.njol.skript.expressions.display.abstr;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;


@Name("Display Brightness Override")
@Description("The block or sky brightness override of a display entity.")
@Examples("set display block brightness override of targeted entity to 15")
public class ExprDisplayBrightness extends SimplePropertyExpression<Entity, Integer> {

	static {
		register(ExprDisplayBrightness.class, Integer.class, "[display] (:block|sky) brightness [override]", "entities");
	}

	private boolean block;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		setExpr((Expression<? extends Entity>) expressions[0]);
		block = parseResult.hasTag("block");
		return true;
	}

	@Override
	public @Nullable Integer convert(Entity from) {
		if (!(from.getEntityMeta() instanceof AbstractDisplayMeta meta)) return null;
		return block ? meta.getBlockLight() : meta.getSkyLight();
	}

	@Override
	public @org.eclipse.jdt.annotation.Nullable Class<?>[] acceptChange(Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) return CollectionUtils.array(Integer.class);
		return null;
	}

	@SuppressWarnings("ConstantValue")
	@Override
	public void change(Event event, @org.jspecify.annotations.Nullable @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) {
		Integer brightness = delta == null ? null : (Integer) delta[0];
		for (Entity entity : getExpr().getArray(event)) {
			if (!(entity.getEntityMeta() instanceof AbstractDisplayMeta meta)) continue;
			switch (mode) {
				case SET -> {
					if (brightness == null) return;
					if (block) meta.setBrightness(brightness, meta.getSkyLight());
					else meta.setBrightness(meta.getBlockLight(), brightness);
				}
				case RESET -> {
					if (block) meta.setBrightness(-1, meta.getSkyLight());
					else meta.setBrightness(meta.getBlockLight(), -1);
				}
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "display " + (block ? "block" : "sky") + " brightness override";
	}

	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}

}
