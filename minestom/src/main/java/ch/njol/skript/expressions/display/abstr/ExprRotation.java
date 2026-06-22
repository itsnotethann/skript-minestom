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
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import org.bukkit.event.Event;

import static com.github.hapily04.skriptminestom.util.NumberUtils.quatFromVec;
import static com.github.hapily04.skriptminestom.util.NumberUtils.vecFromQuat;


@Name("Display Rotation")
@Description("The left or right rotation of a display entity.")
@Examples("set display left rotation of targeted entity to vector(0, 0, 0, 1)")
public class ExprRotation extends SimplePropertyExpression<Entity, Vec> {

	static {
		register(ExprRotation.class, Vec.class, "[display] (:left|right) rotation", "entities");
	}

	private boolean left;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		setExpr((Expression<? extends Entity>) expressions[0]);
		left = parseResult.hasTag("left");
		return true;
	}

	@Override
	public @org.jspecify.annotations.Nullable Vec convert(Entity from) {
		if (!(from.getEntityMeta() instanceof AbstractDisplayMeta meta)) return null;
		return left ? vecFromQuat(meta.getLeftRotation()) : vecFromQuat(meta.getRightRotation());
	}

	@Override
	public @org.eclipse.jdt.annotation.Nullable Class<?>[] acceptChange(Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) return CollectionUtils.array(Vec.class);
		return null;
	}

	@SuppressWarnings("ConstantValue")
	@Override
	public void change(Event event, @org.jspecify.annotations.Nullable @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) {
		Vec vec = delta == null ? null : (Vec) delta[0];
		for (Entity entity : getExpr().getArray(event)) {
			if (!(entity.getEntityMeta() instanceof AbstractDisplayMeta meta)) continue;
			switch (mode) {
				case SET -> {
					if (vec == null) return;
					if (left) meta.setLeftRotation(quatFromVec(vec));
					else meta.setRightRotation(quatFromVec(vec));
				}
				case RESET -> {
					if (left) meta.setLeftRotation(new float[]{0, 0, 0, 1});
					else meta.setRightRotation(new float[]{0, 0, 0, 1});
				}
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "display " + (left ? "left" : "right") + " rotation";
	}

	@Override
	public Class<? extends Vec> getReturnType() {
		return Vec.class;
	}

}
