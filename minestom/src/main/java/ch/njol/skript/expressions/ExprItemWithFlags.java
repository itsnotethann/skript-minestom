package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Item;
import ch.njol.skript.util.ItemFlag;
import ch.njol.skript.util.NBTCompound;
import ch.njol.util.Kleenean;
import com.github.hapily04.skriptminestom.util.NBTUtils;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.item.ItemStack;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;


@Name("Item With Flags")
@Description("An item with specific item flags applied.")
@Examples("# set {_item} to stone with the hide attributes item flag")
public class ExprItemWithFlags extends SimpleExpression<Item> {

	static {
		/*Skript.registerExpression(ExprItemWithFlags.class, Item.class, ExpressionType.COMBINED,
			"%items% with [the] item flag[s] %itemflags%",
			"%items% with [the] %itemflags% item flag[s]",
			"%items% with all [the] item flags");*/
	}

	private Expression<Item> item;
	private Expression<ItemFlag> flags;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		item = (Expression<Item>) expressions[0];
		flags = (Expression<ItemFlag>) expressions[1];
		return true;
	}

	@Override
	protected @Nullable Item[] get(Event event) {
		//NBTCompound nbt = this.flags.getSingle(event);
		Item item = this.item.getSingle(event);
		//item.getItem().withoutExtraTooltip()
		//if (nbt == null || item == null) return new Item[0];
		item = item.copy();
		//item.getItem().withoutExtraTooltip()
		//CompoundBinaryTag incomingCompound = nbt.getCompound();
		CompoundBinaryTag itemCompound = item.getItem().toItemNBT();
		//itemCompound = NBTUtils.mergeItemNBT(itemCompound, incomingCompound, item.getItem());
		ItemStack newItemStack = ItemStack.fromItemNBT(itemCompound);
		item.modify(_ -> newItemStack);
		return new Item[]{item};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends Item> getReturnType() {
		return Item.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return item.toString(event, debug) + " with item flag " + flags.toString(event, debug);
	}

}

