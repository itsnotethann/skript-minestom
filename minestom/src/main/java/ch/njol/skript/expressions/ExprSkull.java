package ch.njol.skript.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Item;
import net.minestom.server.component.DataComponents;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.player.ResolvableProfile;
import org.jspecify.annotations.Nullable;

public class ExprSkull extends SimplePropertyExpression<PlayerSkin, Item> {

	static {
		register(ExprSkull.class, Item.class, "(skull|player[ ]head)[s]", "skins");
	}

	@Override
	public @Nullable Item convert(PlayerSkin from) {
		return new Item(ItemStack.of(Material.PLAYER_HEAD).with(DataComponents.PROFILE, new ResolvableProfile(from)));
	}

	@Override
	protected String getPropertyName() {
		return "skull";
	}

	@Override
	public Class<? extends Item> getReturnType() {
		return Item.class;
	}

}
