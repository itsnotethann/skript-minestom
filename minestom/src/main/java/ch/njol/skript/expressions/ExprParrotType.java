package ch.njol.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.ParrotType;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.avatar.PlayerMeta;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;


@Name("Shoulder Parrot Type")
@Description("The type of parrot on a player's left or right shoulder.")
@Examples("set left shoulder parrot type of player to red")
public class ExprParrotType extends SimplePropertyExpression<Player, ParrotType> {

	static {
		register(ExprParrotType.class, ParrotType.class, "(:left|right) shoulder parrot type", "players");
	}

	private boolean left;

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		left = parseResult.hasTag("left");
		return super.init(expressions, matchedPattern, isDelayed, parseResult);
	}

	@Override
	public @Nullable ParrotType convert(Player from) {
		PlayerMeta playerMeta = from.getPlayerMeta();
		Integer data = left ? playerMeta.getLeftShoulderEntityData() : playerMeta.getRightShoulderEntityData();
		if (data == null) return null;
		return ParrotType.of(data);
	}

	@Override
	public Class<?> @org.jetbrains.annotations.Nullable [] acceptChange(Changer.ChangeMode mode) {
		return switch (mode) {
			case SET, RESET, DELETE -> CollectionUtils.array(ParrotType.class);
			default -> null;
		};
	}

	@Override
	public void change(Event event, Object @org.jetbrains.annotations.Nullable [] delta, Changer.ChangeMode mode) {
		Player[] players = getExpr().getArray(event);
		ParrotType parrotType = delta == null ? null : (ParrotType) delta[0];
		for (Player player : players) {
			PlayerMeta playerMeta = player.getPlayerMeta();
			if (mode == Changer.ChangeMode.RESET || mode == Changer.ChangeMode.DELETE) {
				setShoulderData(playerMeta, null);
				continue;
			}
			if (parrotType == null) return;
			setShoulderData(playerMeta, parrotType.getInternalDataValue());
		}
	}

	@Override
	protected String getPropertyName() {
		return (left ? "left" : "right") + " shoulder parrot type";
	}

	@Override
	public Class<? extends ParrotType> getReturnType() {
		return ParrotType.class;
	}

	private void setShoulderData(PlayerMeta meta, @Nullable Integer entityData) {
		if (left) meta.setLeftShoulderEntityData(entityData);
		else meta.setRightShoulderEntityData(entityData);
	}

}
