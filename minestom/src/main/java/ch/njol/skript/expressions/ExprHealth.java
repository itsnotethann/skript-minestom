package ch.njol.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Enchantment;
import ch.njol.skript.util.Item;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.attribute.Attribute;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;

import static com.github.hapily04.skriptminestom.registration.MinestomClasses.ITEM_CHANGER;

@Name("Health")
@Description("The health of a living entity.")
@Examples("set health of player to 20")
public class ExprHealth extends SimplePropertyExpression<LivingEntity, Number> {

	static {
		register(ExprHealth.class, Number.class, "health", "livingentities");
	}

	@Override
	public @Nullable Number convert(LivingEntity from) {
		return from.getHealth();
	}

	@Override
	public Class<?> @org.jspecify.annotations.Nullable [] acceptChange(Changer.ChangeMode mode) {
		return switch (mode) {
			case SET, REMOVE, ADD, RESET -> CollectionUtils.array(Number.class);
			default -> null;
		};
	}

	@SuppressWarnings("ConstantValue")
	@Override
	public void change(Event event, @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) throws UnsupportedOperationException {
		LivingEntity[] entities = getExpr().getArray(event);
		Float maxHealth = delta == null ? null : ((Number) delta[0]).floatValue();
		for (LivingEntity entity : entities) {
			if (mode == Changer.ChangeMode.RESET) {
				entity.setHealth((float) entity.getAttributeValue(Attribute.MAX_HEALTH));
				continue;
			}
			if (maxHealth == null) return;
			switch (mode) {
				case ADD -> entity.setHealth(entity.getHealth()+maxHealth);
				case REMOVE -> entity.setHealth(entity.getHealth()-maxHealth);
				case SET -> entity.setHealth(maxHealth);
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "health";
	}

	@Override
	public Class<? extends Number> getReturnType() {
		return Number.class;
	}

}
