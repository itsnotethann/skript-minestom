package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Item;
import ch.njol.util.Kleenean;
import com.github.hapily04.skriptminestom.util.NBTUtils;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.TagStringIO;
import net.minestom.server.item.ItemStack;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import java.io.IOException;

@SuppressWarnings("NullableProblems")
public class ExprItemWithNBT extends SimpleExpression<Item> {

	static {
		Skript.registerExpression(ExprItemWithNBT.class, Item.class, ExpressionType.COMBINED, "%item% with nbt %string%");
	}

	private Expression<Item> item;
	private Expression<String> nbt;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		item = (Expression<Item>) expressions[0];
		nbt = (Expression<String>) expressions[1];
		return true;
	}

	@Override
	protected @Nullable Item[] get(Event event) {
		String nbt = this.nbt.getSingle(event);
		Item item = this.item.getSingle(event);
		if (nbt == null || item == null) return new Item[0];
		item = item.copy();
		try {
			CompoundBinaryTag incomingCompound = TagStringIO.tagStringIO().asCompound(nbt);
			CompoundBinaryTag itemCompound = item.getItem().toItemNBT();
			itemCompound = NBTUtils.mergeItemNBT(itemCompound, incomingCompound);
			ItemStack newItemStack = ItemStack.fromItemNBT(itemCompound);
			item.modify(_ -> newItemStack);
			return new Item[]{item};
		} catch (IOException e) {
			Skript.error("Error reading nbt: " + e.getMessage());
			return new Item[0];
		}
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
		return item.toString(event, debug) + " with nbt " + nbt.toString(event, debug);
	}

}
