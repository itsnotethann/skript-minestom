package ch.njol.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.Item;
import ch.njol.util.coll.CollectionUtils;
import com.github.hapily04.skriptminestom.util.ComponentUtils;
import net.kyori.adventure.text.Component;
import net.minestom.server.component.DataComponents;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.scoreboard.Sidebar;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;

@Name("Name")
@Description("The name of a player, entity, or item.")
@Examples("set name of player to \"Custom Name\"")
public class ExprName extends SimplePropertyExpression<Object, Component> {

	private static final Component PLAYER_INVENTORY_TITLE = Component.text("player inventory");

	static {
		register(ExprName.class, Component.class, "[custom[ ]]name", "entities/inventories/items");
	}

	@Override
	public @Nullable Component convert(Object from) {
		if (from instanceof Entity entity) {
			if (entity instanceof Player player) return player.getName();
			Component customName = entity.get(DataComponents.CUSTOM_NAME);
			return customName == null ? Component.text(Classes.toString(entity)) : customName;
		}
		else if (from instanceof AbstractInventory abstractInventory) {
			if (abstractInventory instanceof Inventory inventory) return inventory.getTitle();
			else return PLAYER_INVENTORY_TITLE;
		}
		ItemStack item = ((Item) from).getItem();
		// todo perhaps provide the visual name with item coloring (think enchanted golden apple and steak (material is cooked_beef))
		Component customName = item.get(DataComponents.CUSTOM_NAME);
		if (customName == null) customName = Component.text(Classes.toString(item.material()));
		return customName;
	}

	@Override
	public Class<?> @Nullable [] acceptChange(Changer.ChangeMode mode) {
		Class<?> returnType = getExpr().getReturnType();
		if (Player.class.isAssignableFrom(returnType)) return null;
		if (PlayerInventory.class.isAssignableFrom(returnType)) return null;
		return switch (mode) {
			case RESET, DELETE, SET -> CollectionUtils.array(Component.class);
			default -> null;
		};
	}

	@SuppressWarnings("ConstantValue")
	@Override
	public void change(Event event, @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) throws UnsupportedOperationException {
		Object deltaObject = delta != null && delta.length > 0 ? delta[0] : null;
		Component name = deltaObject == null ? Component.empty() : (Component) deltaObject;
		assert name != null;
		for (Object o : getExpr().getArray(event)) {
			switch (o) {
				case Item item -> item.modify(i -> i.withCustomName(name), true);
				case Inventory nameableInventory -> nameableInventory.setTitle(name);
				case Entity e -> e.set(DataComponents.CUSTOM_NAME, name);
				default -> {}
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "name";
	}

	@Override
	public Class<? extends Component> getReturnType() {
		return Component.class;
	}

}
