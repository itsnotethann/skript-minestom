package ch.njol.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Item;
import net.minestom.server.component.DataComponents;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.player.ResolvableProfile;
import org.jspecify.annotations.Nullable;

@Name("Skull Item")
@Description("Creates a skull item from a skin.")
@Examples("""
	give player skull of skin from "jeb_"
	give player skull of player # doesn't fetch skin""")
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
