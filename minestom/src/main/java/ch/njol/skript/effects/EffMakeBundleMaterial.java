package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Item;
import ch.njol.util.Kleenean;
import net.minestom.server.entity.Player;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.common.TagsPacket;
import net.minestom.server.registry.RegistryKey;
import net.minestom.server.registry.TagKey;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Bundle Material")
@Description("""
	Make the provided players' clients view the provided items' material as bundle-acting (so you can detect scrolling).
	If you, for some reason, need more than one item, you must send all of the extra items you want in one effect, otherwise the old ones
	will not work.""")
public class EffMakeBundleMaterial extends Effect {

	static {
		Skript.registerEffect(EffMakeBundleMaterial.class, "make %items% [act like] a bundle material for %players%");
	}

	private Expression<Item> items;
	private Expression<Player> players;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		this.items = (Expression<Item>) expressions[0];
		this.players = (Expression<Player>) expressions[1];
		return true;
	}

	@Override
	protected void execute(Event event) {
		// hacky registry shenanigans
		List<Integer> items = new ArrayList<>();
		for (RegistryKey<Material> key : Material.staticRegistry().getTag(TagKey.ofHash("#minecraft:bundles"))) {
			items.add(Material.fromKey(key.key()).id());
		}

		for (Item item : this.items.getArray(event)) {
			items.add(item.getItem().material().id());
		}

		int[] items2 = new int[items.size()];
		for (int i = 0; i < items.size(); i++) {
			items2[i] = items.get(i);
		}

		TagsPacket tagsPacket = new TagsPacket(List.of(new TagsPacket.Registry("minecraft:item", List.of(new TagsPacket.Tag("minecraft:bundles", items2)))));
		// send to players
		for (Player player : players.getArray(event)) {
			player.sendPacket(tagsPacket);
		}
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "make " + items.toString(event, debug) + " a bundle material for " + players.toString(event, debug);
	}

}
