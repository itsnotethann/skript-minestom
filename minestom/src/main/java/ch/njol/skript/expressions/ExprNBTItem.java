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
import ch.njol.skript.util.NBTCompound;
import ch.njol.util.Kleenean;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.MinecraftServer;
import net.minestom.server.component.DataComponents;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.component.CustomData;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;


@Name("NBT Item")
@Description("Creates an item from NBT or returns the full item NBT of items.")
@Examples("set {_item} to item from {_nbt}")
public class ExprNBTItem extends SimpleExpression<Object> {

	static {
		Skript.registerExpression(ExprNBTItem.class, Object.class, ExpressionType.COMBINED,
			"item from %nbtcompound%",
			"full item nbt[ ][compound[s]] (of|from) %items%");
	}

	private Expression<?> expression;
	private boolean item;

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		expression = expressions[0];
		item = matchedPattern == 0;
		return true;
	}

	@Override
	protected @Nullable Object[] get(Event event) {
		if (item) {
			NBTCompound compound = (NBTCompound) expression.getSingle(event);
			if (compound == null) return new Item[0];
			return new Item[]{new Item(ItemStack.fromItemNBT(compound.getCompound()))};
		}
		Item[] items = (Item[]) expression.getArray(event);
		NBTCompound[] compounds = new NBTCompound[items.length];
		for (int i = 0; i < items.length; i++) {
			ItemStack item = items[i].getItem();
			CompoundBinaryTag customData = item.get(DataComponents.CUSTOM_DATA, new CustomData(CompoundBinaryTag.empty())).nbt();
			customData = customData.putInt("DataVersion", MinecraftServer.DATA_VERSION);
			compounds[i] = new NBTCompound(item.with(DataComponents.CUSTOM_DATA, new CustomData(customData)).toItemNBT(MinecraftServer.getRegistries()), false);
		}
		return compounds;
	}

	@Override
	public boolean isSingle() {
		return expression.isSingle();
	}

	@Override
	public Class<?> getReturnType() {
		return item ? Item.class : NBTCompound.class;
	}

	@Override
	public String toString(@org.eclipse.jdt.annotation.Nullable Event event, boolean debug) {
		if (item) return "item from " + expression.toString(event, debug);
		return "full nbt from " + expression.toString(event, debug);
	}

}
