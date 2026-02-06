package ch.njol.skript.events;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.events.wrapper.EventWrapper;
import ch.njol.skript.events.wrapper.ItemDropWrapper;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Item;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Player Item Drop")
@Description("Called when a player drops an item.")
@Examples("on item drop of stone:")
public class EvtItemDrop extends SkriptEvent {

	static {
		Skript.registerEvent("Player Item Drop", EvtItemDrop.class, ItemDropWrapper.class, "[player] [item] drop[ping] [[of] %-item%]");
	}

	private Literal<Item> item;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Literal<?> @NotNull [] args, int matchedPattern, SkriptParser.@NotNull ParseResult parseResult) {
		item = (Literal<Item>) args[0];
		return true;
	}

	// todo maybe support item name/lore later
	@SuppressWarnings("unchecked")
	@Override
	public boolean check(@NotNull Event event) {
		ItemDropEvent itemDropEvent = ((EventWrapper<ItemDropEvent>) event).getEvent();
		ItemStack dropStack = itemDropEvent.getItemStack();
		Material dropMaterial = dropStack.material();
		Item i = item == null ? null : item.getSingle(event);
		Material itemMaterial;
		if (i == null) itemMaterial = dropMaterial;
		else itemMaterial = i.getItem().material();
		return itemMaterial.equals(dropMaterial);
	}

	@Override
	public @NotNull String toString(@Nullable Event event, boolean debug) {
		String toString = "player item drop";
		if (item != null) toString += " of " + item.toString(event, debug);
		return toString;
	}

}
