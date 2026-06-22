package ch.njol.skript.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.util.Item;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jspecify.annotations.Nullable;


@Name("Item From Block")
@Description("The item representation of a block.")
@Examples("set {_item} to item of block at player")
public class ExprItemFromBlock extends SimplePropertyExpression<Block, Item> {

	static {
		register(ExprItemFromBlock.class, Item.class, "item[s]", "blocks");
	}

	@Override
	public @Nullable Item convert(Block from) {
		Material material = from.registry().material();
		if (material == null) return null;
		return new Item(ItemStack.of(material));
	}

	@Override
	protected String getPropertyName() {
		return "item";
	}

	@Override
	public Class<? extends Item> getReturnType() {
		return Item.class;
	}

}
