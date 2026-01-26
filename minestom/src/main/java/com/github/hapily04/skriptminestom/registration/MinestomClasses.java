package com.github.hapily04.skriptminestom.registration;

import ch.njol.skript.SkriptConfig;
import ch.njol.skript.classes.*;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.Enchantment;
import ch.njol.skript.util.Item;
import ch.njol.skript.util.Slot;
import ch.njol.skript.variables.Variables;
import ch.njol.util.coll.CollectionUtils;
import ch.njol.yggdrasil.Fields;
import com.github.hapily04.skriptminestom.util.NumberUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.TagStringIO;
import net.kyori.adventure.resource.ResourcePackStatus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.*;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.SharedInstance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.inventory.EquipmentHandler;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.registry.RegistryKey;
import net.minestom.server.scoreboard.Sidebar;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.lang.comparator.Comparators;
import org.skriptlang.skript.lang.converter.Converters;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.util.Arrays;
import java.util.Locale;

public class MinestomClasses {

	// TODO Use lang files and enumclassinfo

	public static final Changer<Item> ITEM_CHANGER = new Changer<>() {
		@Override
		public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
			return switch (mode) {
				case DELETE, SET -> CollectionUtils.array(Item.class);
				case REMOVE, ADD -> CollectionUtils.array(Enchantment[].class);
				default -> null;
			};
		}

		@Override
		public void change(Item[] what, @org.jetbrains.annotations.Nullable @Nullable Object[] delta, ChangeMode mode) {
			for (Item item : what) {
				switch (mode) {
					case DELETE -> item.modify(_ -> ItemStack.AIR, true);
					case SET -> {
						Item changeItem = (Item) delta[0];
						if (changeItem == null) continue;
						item.modify(_ -> changeItem.getItem(), true);
					}
					case ADD -> {
						Enchantment[] enchantments = Arrays.copyOf(delta, delta.length, Enchantment[].class);
						Enchantment.add(item, true, enchantments);
					}
					case REMOVE -> {
						Enchantment[] enchantments = Arrays.copyOf(delta, delta.length, Enchantment[].class);
						Enchantment.remove(item, enchantments);
					}
				}
			}
		}
	};

	public static void register() {
		/*
		 * Classes
		 */
		Classes.registerClass(new ClassInfo<>(CommandSender.class, "commandsender")
			.user("command ?senders?")
			.name("Command Sender")
			.description("Something that can execute a command and receive messages (players/console).")
			.defaultExpression(new EventValueExpression<>(CommandSender.class))
			.parser(new Parser<>() {
				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return false;
				}

				@Override
				public @NotNull String toString(@NotNull CommandSender o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull CommandSender o) {
					return o instanceof Player player ? player.getUsername() : "console";
				}
			}));
		Classes.registerClass(new ClassInfo<>(ConsoleSender.class, "consolesender")
			.user("console ?senders?")
			.name("Console Sender")
			.description("The console.")
			.defaultExpression(new EventValueExpression<>(ConsoleSender.class))
			.parser(new Parser<ConsoleSender>() {
				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return false;
				}

				@Override
				public @NotNull String toString(@NotNull ConsoleSender o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull ConsoleSender o) {
					return "console";
				}
			}));
		Classes.registerClass(new ClassInfo<>(Player.class, "player")
			.user("players?")
			.name("Player")
			.description("A entity of type Player with a connection to the server.")
			.defaultExpression(new EventValueExpression<>(Player.class))
			.parser(new Parser<>() {
				@Nullable
				public Player parse(@NotNull String s, @NotNull ParseContext context) {
					return MinecraftServer.getConnectionManager().findOnlinePlayer(s);
				}

				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return context == ParseContext.COMMAND || context == ParseContext.PARSE;
				}

				@Override
				public @NotNull String toString(@NotNull Player o, int flags) {
					return o.getUsername();
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull Player o) {
					return SkriptConfig.usePlayerUUIDsInVariableNames.value() ? o.getUuid().toString() : toString(o, 0);
				}
			})
			.changer(new Changer<>() {
				@SuppressWarnings("DataFlowIssue")
				@Override
				public @Nullable Class<?> @NotNull [] acceptChange(@NotNull ChangeMode mode) {
					return switch (mode) {
						case ADD, REMOVE -> CollectionUtils.array(AbstractInventory[].class, Item[].class);
						case REMOVE_ALL -> CollectionUtils.array(Item[].class);
						default -> null;
					};
				}

				@Override
				public void change(Player @NotNull [] what, @Nullable Object @NotNull [] delta, @NotNull ChangeMode mode) {
					for (Player player : what) {
						AbstractInventory inventory = player.getInventory();
						inventoryChange(delta, mode, inventory);
					}
				}
			}));
		Classes.registerClass(new ClassInfo<>(Entity.class, "entity")
			.user("entit(y|ies)")
			.name("Entity")
			.description("A mob/player/physical non-block object in an instance.")
			.defaultExpression(new EventValueExpression<>(Entity.class))
			.parser(new Parser<>() {
				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return false;
				}

				@Override
				public @NotNull String toString(@NotNull Entity o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull Entity o) {
					return o.getEntityType().name().toLowerCase(Locale.ENGLISH);
				}
			}));
		Classes.registerClass(new ClassInfo<>(LivingEntity.class, "livingentity")
			.user("living ?entit(y|ies)")
			.name("Living Entity")
			.description("An entity that has health, armor, and a main/offhand.")
			.defaultExpression(new EventValueExpression<>(LivingEntity.class))
			.parser(new Parser<>() {
				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return false;
				}

				@Override
				public @NotNull String toString(@NotNull LivingEntity o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull LivingEntity o) {
					return o.getEntityType().name().toLowerCase(Locale.ENGLISH);
				}
			}));
		Classes.registerClass(new ClassInfo<>(EntityCreature.class, "entitycreature")
			.user("entity ?creatures?")
			.name("Entity Creature")
			.description("An entity that has health, armor, main/offhand, and is able to pathfind.")
			.defaultExpression(new EventValueExpression<>(EntityCreature.class))
			.parser(new Parser<>() {
				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return false;
				}

				@Override
				public @NotNull String toString(@NotNull EntityCreature o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull EntityCreature o) {
					return o.getEntityType().name().toLowerCase(Locale.ENGLISH);
				}
			}));
		Classes.registerClass(new ClassInfo<>(EquipmentHandler.class, "equipmenthandler")
			.user("equipment ?handlers?")
			.name("Equipment Handler")
			.description("An entity that is capable of bearing armor and off/main hand tools.")
			.defaultExpression(new EventValueExpression<>(EquipmentHandler.class))
			.parser(new Parser<>() {
				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return false;
				}

				@Override
				public @NotNull String toString(@NotNull EquipmentHandler o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull EquipmentHandler o) {
					return "equipment handler"; // don't think we can make this better
				}
			}));
		Classes.registerClass(new ClassInfo<>(Pos.class, "position")
			.user("positions?")
			.name("Position")
			.description("A location with an x, y, z, yaw, and pitch. An instance is not attached to this type.")
			.defaultExpression(new EventValueExpression<>(Pos.class))
			.parser(new Parser<>() {
				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return false;
				}

				@Override
				public @NotNull String toString(@NotNull Pos o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull Pos o) {
					return "position: x" + o.x() + " y: " + o.y() + " z: " + o.z() + " yaw: " + o.yaw() + " pitch: " + o.pitch();
				}
			})
			.serializer(new Serializer<>() {
				@Override
				public @NotNull Fields serialize(@NotNull Pos o) {
					Fields fields = new Fields();
					fields.putPrimitive("x", o.x());
					fields.putPrimitive("y", o.y());
					fields.putPrimitive("z", o.z());
					fields.putPrimitive("yaw", o.yaw());
					fields.putPrimitive("pitch", o.pitch());
					return fields;
				}

				@Override
				public void deserialize(@NotNull Pos o, @NotNull Fields f) {
					assert false;
				}

				@Override
				protected @NotNull Pos deserialize(@NotNull Fields f) throws StreamCorruptedException {
					double x = f.getPrimitive("x", double.class);
					double y = f.getPrimitive("y", double.class);
					double z = f.getPrimitive("z", double.class);
					float yaw = f.getPrimitive("yaw", float.class);
					float pitch = f.getPrimitive("pitch", float.class);
					return new Pos(x, y, z, yaw, pitch);
				}

				@Override
				public boolean mustSyncDeserialization() {
					return false;
				}

				@Override
				protected boolean canBeInstantiated() {
					return false;
				}
			}));
		Classes.registerClass(new ClassInfo<>(Vec.class, "vector")
			.user("vectors?")
			.name("Vector")
			.description("An object with 3 values: x, y, z. Can be used as a location, but position is used more often for that use-case.")
			.defaultExpression(new EventValueExpression<>(Vec.class))
			.parser(new Parser<>() {
				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return false;
				}

				@Override
				public @NotNull String toString(@NotNull Vec o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull Vec o) {
					return "vector: x" + o.x() + " y: " + o.y() + " z: " + o.z();
				}
			})
			.serializer(new Serializer<>() {
				@Override
				public @NotNull Fields serialize(@NotNull Vec o) {
					Fields fields = new Fields();
					fields.putPrimitive("x", o.x());
					fields.putPrimitive("y", o.y());
					fields.putPrimitive("z", o.z());
					return fields;
				}

				@Override
				public void deserialize(@NotNull Vec o, @NotNull Fields f) {
					assert false;
				}

				@Override
				protected @NotNull Vec deserialize(@NotNull Fields f) throws StreamCorruptedException {
					double x = f.getPrimitive("x", double.class);
					double y = f.getPrimitive("y", double.class);
					double z = f.getPrimitive("z", double.class);
					return new Vec(x, y, z);
				}

				@Override
				public boolean mustSyncDeserialization() {
					return false;
				}

				@Override
				protected boolean canBeInstantiated() {
					return false;
				}
			}));
		Classes.registerClass(new ClassInfo<>(Point.class, "point")
			.user("points?")
			.name("Point")
			.description("An object with 3 values: x, y, z. Is internally either a block vector, vector, or position.")
			.defaultExpression(new EventValueExpression<>(Point.class))
			.parser(new Parser<>() {
				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return false;
				}

				@Override
				public @NotNull String toString(@NotNull Point o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull Point o) {
					return "point: x" + o.x() + " y: " + o.y() + " z: " + o.z();
				}
			})); // don't think a serializer is needed as it should go to blockvec/vector/position
		Classes.registerClass(new ClassInfo<>(BlockVec.class, "blockvector")
			.user("block ?vectors?")
			.name("Block Vector")
			.description("A vector with the x, y, and z without decimals.")
			.defaultExpression(new EventValueExpression<>(BlockVec.class))
			.parser(new Parser<>() {
				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return false;
				}

				@Override
				public @NotNull String toString(@NotNull BlockVec o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull BlockVec o) {
					return "blockvector: x" + o.x() + " y: " + o.y() + " z: " + o.z();
				}
			})
			.serializer(new Serializer<>() {
				@Override
				public @NotNull Fields serialize(@NotNull BlockVec o) {
					Fields fields = new Fields();
					fields.putPrimitive("x", o.x());
					fields.putPrimitive("y", o.y());
					fields.putPrimitive("z", o.z());
					return fields;
				}

				@Override
				public void deserialize(@NotNull BlockVec o, @NotNull Fields f) {
					assert false;
				}

				@Override
				protected @NotNull BlockVec deserialize(@NotNull Fields f) throws StreamCorruptedException {
					double x = f.getPrimitive("x", int.class);
					double y = f.getPrimitive("y", int.class);
					double z = f.getPrimitive("z", int.class);
					return new BlockVec(x, y, z);
				}

				@Override
				public boolean mustSyncDeserialization() {
					return false;
				}

				@Override
				protected boolean canBeInstantiated() {
					return false;
				}
			}));
		Classes.registerClass(new ClassInfo<>(Instance.class, "instance")
			.user("instances?")
			.name("Instance")
			.description("A world consisting of blocks and entities.")
			.defaultExpression(new EventValueExpression<>(Instance.class))
			.parser(new Parser<>() {
				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return false;
				}

				@Override
				public @NotNull String toString(@NotNull Instance o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull Instance o) {
					return "instance uuid: " + o.getUuid();
				}
			}));
		Classes.registerClass(new ClassInfo<>(InstanceContainer.class, "instancecontainer")
			.user("instance ?containers?")
			.name("Instance Container")
			.description("A world consisting of blocks and entities.")
			.defaultExpression(new EventValueExpression<>(InstanceContainer.class))
			.parser(new Parser<>() {
				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return false;
				}

				@Override
				public @NotNull String toString(@NotNull InstanceContainer o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull InstanceContainer o) {
					return "instance container uuid: " + o.getUuid();
				}
			}));
		Classes.registerClass(new ClassInfo<>(SharedInstance.class, "sharedinstance")
			.user("shared ?instances?")
			.name("Shared Instance")
			.description("A world sharing the blocks from its underlying Instance Container. Entities are not shared.")
			.defaultExpression(new EventValueExpression<>(SharedInstance.class))
			.parser(new Parser<>() {
				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return false;
				}

				@Override
				public @NotNull String toString(@NotNull SharedInstance o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull SharedInstance o) {
					return "shared instance uuid: " + o.getUuid();
				}
			}));
		Classes.registerClass(new ClassInfo<>(Block.class, "block")
			.user("blocks?")
			.name("Block")
			.description("A block with a type, properties (blockdata), nbt, and handler.")
			.defaultExpression(new EventValueExpression<>(Block.class))
			.parser(new Parser<>() {
				@Nullable
				public Block parse(@NotNull String s, @NotNull ParseContext context) {
					s = s.toLowerCase(Locale.ENGLISH);
					int endChar;
					if (s.contains("[")) {
						if (!s.endsWith("]")) return null;
						endChar = s.indexOf('[')-1;
					}
					else endChar = s.length()-1;
					String nameSpace = s.substring(0, endChar+1);
					nameSpace = nameSpace.replace(' ', '_');
					if (!nameSpace.contains(":")) nameSpace = "minecraft:" + nameSpace;
					else if (!nameSpace.startsWith("minecraft:")) return null; // only minecraft: is supported since you can't add mod blocks anyway atm
					if (!Key.parseable(nameSpace)) return null;
					return Block.fromState(nameSpace);
				}

				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return true;
				}

				@Override
				public @NotNull String toString(@NotNull Block o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull Block o) {
					return o.state();
				}
			})
			.serializer(new Serializer<>() {
				@Override
				public @NotNull Fields serialize(@NotNull Block o) {
					Fields fields = new Fields();
					fields.putPrimitive("id", o.stateId());
					return fields;
				}

				@Override
				public void deserialize(@NotNull Block o, @NotNull Fields f) {
					assert false;
				}

				@Override
				protected @NotNull Block deserialize(@NotNull Fields f) throws StreamCorruptedException {
					int id = f.getPrimitive("id", int.class);
					Block block = Block.fromStateId(id);
					if (block == null)
						throw new StreamCorruptedException("Block with state id '" + id + "' was not found.");
					return block;
				}

				@Override
				public boolean mustSyncDeserialization() {
					return false;
				}

				@Override
				protected boolean canBeInstantiated() {
					return false;
				}
			}));
		Classes.registerClass(new ClassInfo<>(GameMode.class, "gamemode")
			.user("game ?modes?")
			.name("Game Mode")
			.description("Creative, survival, spectator, and adventure.")
			.defaultExpression(new EventValueExpression<>(GameMode.class))
			.parser(new Parser<>() {
				@Nullable
				public GameMode parse(@NotNull String s, @NotNull ParseContext context) {
					s = s.toUpperCase(Locale.ENGLISH);
					for (GameMode gameMode : GameMode.values()) {
						if (gameMode.name().equals(s)) return gameMode;
					}
					return null;
				}

				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return true;
				}

				@Override
				public @NotNull String toString(@NotNull GameMode o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull GameMode o) {
					return o.name().toLowerCase(Locale.ENGLISH);
				}
			})
			.serializer(new EnumSerializer<>(GameMode.class)));
		Classes.registerClass(new ClassInfo<>(Component.class, "component")
			.user("components?")
			.name("Component")
			.description("A piece of text with formatting.")
			.defaultExpression(new EventValueExpression<>(Component.class))
			.parser(new Parser<>() {
				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return false;
				}

				@Override
				public @NotNull String toString(@NotNull Component o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull Component o) {
					return PlainTextComponentSerializer.plainText().serialize(o);
				}
			}));
		Classes.registerClass(new ClassInfo<>(TagResolver.class, "tagresolver")
			.user("tag ?resolvers?")
			.name("Tag Resolver")
			.description("Replace within the text for MiniMessage.")
			.defaultExpression(new EventValueExpression<>(TagResolver.class))
			.parser(new Parser<>() {
				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return false;
				}

				@Override
				public @NotNull String toString(@NotNull TagResolver o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull TagResolver o) {
					return o.toString();
				}
			}));
		Classes.registerClass(new ClassInfo<>(ResourcePackStatus.class, "resourcepackstatus")
			.user("resource ?pack ?status(es)?")
			.name("Resource Pack Status")
			.description("The status of a resource pack that was sent.")
			.defaultExpression(new EventValueExpression<>(ResourcePackStatus.class))
			.parser(new Parser<>() {
				@Nullable
				public ResourcePackStatus parse(@NotNull String s, @NotNull ParseContext context) {
					s = s.toUpperCase(Locale.ENGLISH);
					s = s.replace(' ', '_');
					for (ResourcePackStatus status : ResourcePackStatus.values()) {
						if (status.name().equals(s)) return status;
					}
					return null;
				}

				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return true;
				}

				@Override
				public @NotNull String toString(@NotNull ResourcePackStatus o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull ResourcePackStatus o) {
					return o.name().toLowerCase(Locale.ENGLISH).replace('_', ' ');
				}
			})
			.serializer(new EnumSerializer<>(ResourcePackStatus.class)));
		Classes.registerClass(new ClassInfo<>(AbstractInventory.class, "inventory")
			.user("inventor(y|ies)")
			.name("Inventory")
			.description("An inventory (player's inventory, anvil inventory, etc.)")
			.defaultExpression(new EventValueExpression<>(AbstractInventory.class))
			.parser(new Parser<>() {
				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return false;
				}

				@Override
				public @NotNull String toString(@NotNull AbstractInventory o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull AbstractInventory o) {
					String name;
					if (o instanceof Inventory inventory) name = inventory.getInventoryType().name().toLowerCase(Locale.ENGLISH);
					else name = "player";
					return name + " inventory";
				}
			})
			.changer(new Changer<>() {
				@SuppressWarnings("DataFlowIssue")
				@Override
				public @Nullable Class<?> @NotNull [] acceptChange(@NotNull ChangeMode mode) {
					return switch (mode) {
						case ADD, REMOVE -> CollectionUtils.array(AbstractInventory[].class, Item[].class);
						case REMOVE_ALL -> CollectionUtils.array(Item[].class);
						default -> null;
					};
				}

				@Override
				public void change(AbstractInventory @NotNull [] what, @Nullable Object @NotNull [] delta, @NotNull ChangeMode mode) {
					for (AbstractInventory inventory : what) {
						inventoryChange(delta, mode, inventory);
					}
				}
			}));
		/*Classes.registerClass(new ClassInfo<>(ItemStack.class, "itemstack")
			.user("item ?stacks?")
			.name("Item Stack")
			.description("An item.")
			.defaultExpression(new EventValueExpression<>(ItemStack.class))
			.serializer(new Serializer<>() {
				@Override
				public @NotNull Fields serialize(@NotNull ItemStack o) {
					Fields fields = new Fields();
					try {
						fields.putPrimitive("item-nbt", TagStringIO.tagStringIO().asString(o.toItemNBT()));
					} catch (IOException e) {
						System.err.println("Error while trying to serialize itemstack: " + e.getMessage());
					}
					return fields;
				}

				@Override
				public void deserialize(@NotNull ItemStack o, @NotNull Fields f) {
					assert false;
				}

				@Override
				protected @NotNull ItemStack deserialize(@NotNull Fields f) throws StreamCorruptedException {
					try {
						return new Item(ItemStack.fromItemNBT(TagStringIO.tagStringIO().asCompound((String) f.getPrimitive("item-nbt"))));
					} catch (IOException e) {
						throw new StreamCorruptedException("Error occurred whilst trying to deserialize an itemstack.");
					}
				}

				@Override
				public boolean mustSyncDeserialization() {
					return false;
				}

				@Override
				protected boolean canBeInstantiated() {
					return false;
				}
			}));*/
		Classes.registerClass(new ClassInfo<>(Item.class, "item")
			.user("items?")
			.name("Item")
			.description("An item.")
			.defaultExpression(new EventValueExpression<>(Item.class))
			.parser(new Parser<>() {
				@SuppressWarnings("PatternValidation")
				@Nullable
				public Item parse(@NotNull String s, @NotNull ParseContext context) {
					s = s.toLowerCase(Locale.ENGLISH);
					String[] parts = s.split(" ");;
					int amount = 1;
					int materialIndex = 0;
					if (parts.length >= 2) {
						String amountPart = parts[0];
						if (NumberUtils.isOnlyDigits(amountPart)) {
							if (!NumberUtils.isInteger(amountPart)) return null;
							amount = Integer.parseInt(amountPart);
							materialIndex = 1;
						}
					}
					String[] choppedParts = new String[parts.length-materialIndex];
					System.arraycopy(parts, materialIndex, choppedParts, 0, parts.length-materialIndex);
					String nameSpace = String.join("_", choppedParts);
					if (!nameSpace.contains(":")) nameSpace = "minecraft:" + nameSpace;
					else if (!nameSpace.startsWith("minecraft:")) return null; // only minecraft: is supported since you can't add mod items anyways atm
					if (!Key.parseable(nameSpace)) return null;
					Material material = Material.fromKey(nameSpace);
					if (material == null) return null;
					return new Item(ItemStack.of(material, amount));
				}

				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return true;
				}

				@Override
				public @NotNull String toString(@NotNull Item o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull Item o) {
					ItemStack item = o.getItem();
					return item.amount() + " " + keyToString(item.material().key());
				}
			})
			.serializer(new Serializer<>() {
				@Override
				public @NotNull Fields serialize(@NotNull Item o) throws NotSerializableException {
					Fields fields = new Fields();
					try {
						fields.putObject("item-nbt", TagStringIO.tagStringIO().asString(o.getItem().toItemNBT()));
					} catch (IOException e) {
						throw new NotSerializableException("Error whilst trying to to serialize an item: " + e.getMessage());
					}
					return fields;
				}

				@Override
				public void deserialize(@NotNull Item o, @NotNull Fields f) {
					assert false;
				}

				@Override
				protected @NotNull Item deserialize(@NotNull Fields f) throws StreamCorruptedException {
					try {
						Object nbt = f.getObject("item-nbt");
						if (nbt == null) throw new StreamCorruptedException("Error occurred whilst trying to deserialize an itemstack.");
						return new Item(ItemStack.fromItemNBT(TagStringIO.tagStringIO().asCompound((String) nbt)));
					} catch (IOException e) {
						throw new StreamCorruptedException("Error occurred whilst trying to deserialize an itemstack.");
					}
				}

				@Override
				public boolean mustSyncDeserialization() {
					return false;
				}

				@Override
				protected boolean canBeInstantiated() {
					return false;
				}
			})
			.changer(ITEM_CHANGER));
		Classes.registerClass(new ClassInfo<>(Slot.class, "slot")
			.user("slots?")
			.name("Slot")
			.description("Represents an item in a slot in an inventory.")
			.defaultExpression(new EventValueExpression<>(Slot.class))
			.serializeAs(Item.class)
			.changer(ITEM_CHANGER));
		Classes.registerClass(new ClassInfo<>(EntityType.class, "entitytype")
			.user("entity ?types?")
			.name("Entity Type")
			.description("The type of an entity (zombie, player, skeleton, etc.)")
			.defaultExpression(new EventValueExpression<>(EntityType.class))
			.parser(new Parser<>() {
				@Nullable
				public EntityType parse(@NotNull String s, @NotNull ParseContext context) {
					s = s.toLowerCase(Locale.ENGLISH).replace(' ', '_');
					if (!s.contains("minecraft:")) s = "minecraft:" + s;
					if (!Key.parseable(s)) return null;
					return EntityType.fromKey(s);
				}

				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return true;
				}

				@Override
				public @NotNull String toString(@NotNull EntityType o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull EntityType o) {
					return o.key().asString();
				}
			})
			.serializer(new Serializer<>() {
				@Override
				public @NotNull Fields serialize(@NotNull EntityType o) throws NotSerializableException {
					Fields fields = new Fields();
					fields.putPrimitive("entity-type", o.key().asString());
					return fields;
				}

				@Override
				public void deserialize(@NotNull EntityType o, @NotNull Fields f) throws StreamCorruptedException {
					assert false;
				}

				@Override
				protected @NotNull EntityType deserialize(@NotNull Fields f) throws StreamCorruptedException {
					String key = f.getPrimitive("entity-type", String.class);
					EntityType type = EntityType.fromKey(key);
					if (type == null)
						throw new StreamCorruptedException("Can't deserialize entity type from key: " + key);
					return type;
				}

				@Override
				public boolean mustSyncDeserialization() {
					return false;
				}

				@Override
				protected boolean canBeInstantiated() {
					return false;
				}
			}));
		Classes.registerClass(new ClassInfo<>(EquipmentSlot.class, "equipmentslot")
			.user("equipment ?slots?")
			.name("Equipment Slot")
			.description("") // todo
			.defaultExpression(new EventValueExpression<>(EquipmentSlot.class))
			.parser(new Parser<>() {
				@Nullable
				public EquipmentSlot parse(@NotNull String s, @NotNull ParseContext context) {
					s = s.toUpperCase(Locale.ENGLISH).replace(' ', '_');
					for (EquipmentSlot slo : EquipmentSlot.values()) {
						if (slo.name().equals(s)) return slo;
					}
					return null;
				}

				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return true;
				}

				@Override
				public @NotNull String toString(@NotNull EquipmentSlot o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull EquipmentSlot o) {
					return o.name().toLowerCase(Locale.ENGLISH).replace('_', ' ') + " slot";
				}
			})
			.serializer(new EnumSerializer<>(EquipmentSlot.class)));
		Classes.registerClass(new ClassInfo<>(Sidebar.class, "scoreboard")
			.user("score ?boards?")
			.name("Scoreboard")
			.description("The scoreboard on the side of a player's screen")
			.defaultExpression(new EventValueExpression<>(Sidebar.class))
			.parser(new Parser<>() {
				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return false;
				}

				@Override
				public @NotNull String toString(@NotNull Sidebar o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull Sidebar o) {
					return "scoreboard titled \"" + PlainTextComponentSerializer.plainText().serialize(o.getTitle()) + "\"";
				}
			}));
		Classes.registerClass(new ClassInfo<>(Material.class, "material")
			.user("materials?")
			.name("Material")
			.description("A material. Only used for ExprName, see type \"Item\"")
			.defaultExpression(new EventValueExpression<>(Material.class))
			.parser(new Parser<>() {
				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return false;
				}

				@Override
				public @NotNull String toString(@NotNull Material o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull Material o) {
					return keyToString(o.key());
				}
			}));
		Classes.registerClass(new ClassInfo<>(Enchantment.class, "enchantment")
			.user("enchantments?")
			.name("Enchantment")
			.description("An enchantment for an item (sharpness 1, knockback, knockback 1, etc.)")
			.defaultExpression(new EventValueExpression<>(Enchantment.class))
			.parser(new Parser<>() {
				@Nullable
				public Enchantment parse(@NotNull String s, @NotNull ParseContext context) {
					s = s.toLowerCase(Locale.ENGLISH);
					String[] parts = s.split(" ");;
					int level = -1;
					int hasLevel = 0;
					if (parts.length >= 2) {
						String levelPart = parts[parts.length-1];
						if (NumberUtils.isOnlyDigits(levelPart)) {
							if (!NumberUtils.isInteger(levelPart)) return null;
							level = Integer.parseInt(levelPart);
							hasLevel = 1;
						}
					}
					String[] choppedParts = new String[parts.length-(hasLevel)];
					System.arraycopy(parts, 0, choppedParts, 0, choppedParts.length);
					String nameSpace = String.join("_", choppedParts);
					if (!nameSpace.contains(":")) nameSpace = "minecraft:" + nameSpace;
					else if (!nameSpace.startsWith("minecraft:")) return null;
					if (!Key.parseable(nameSpace)) return null;
					RegistryKey<net.minestom.server.item.enchant.Enchantment> enchant = MinecraftServer.getEnchantmentRegistry().getKey(Key.key(nameSpace));
					if (enchant == null) return null;
					return new Enchantment(enchant, level);
				}

				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return true;
				}

				@Override
				public @NotNull String toString(@NotNull Enchantment o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull Enchantment o) {
					int level = o.level();
					return keyToString(o.enchantment().key()) + (level > 0 ? " " + level : "");
				}
			})
			.serializer(new Serializer<>() {
				@Override
				public @NotNull Fields serialize(@NotNull Enchantment o) {
					Fields fields = new Fields();
					fields.putObject("id", o.enchantment().key().asString());
					fields.putPrimitive("level", o.level());
					return fields;
				}

				@Override
				public void deserialize(@NotNull Enchantment o, @NotNull Fields f) {
					assert false;
				}

				@Override
				protected @NotNull Enchantment deserialize(@NotNull Fields f) throws StreamCorruptedException {
					String id = f.getObject("id", String.class);
					assert id != null;
					RegistryKey<net.minestom.server.item.enchant.Enchantment> enchantment = MinecraftServer.getEnchantmentRegistry().getKey(Key.key(id));
					if (enchantment == null) throw new StreamCorruptedException("Enchantment with id '" + id + "' was not found.");
					int level = f.getPrimitive("level", int.class);
					return new Enchantment(enchantment, level);
				}

				@Override
				public boolean mustSyncDeserialization() {
					return false;
				}

				@Override
				protected boolean canBeInstantiated() {
					return false;
				}
			}));

		/*
		 * Converters
		 */
		Converters.registerConverter(String.class, Component.class, Component::text);
		Converters.registerConverter(Component.class, String.class, c -> LegacyComponentSerializer.legacyAmpersand().serialize(c));
		Converters.registerConverter(CommandSender.class, Player.class, from -> {
			if (from instanceof Player player) return player;
			return null;
		});
		Converters.registerConverter(CommandSender.class, ConsoleSender.class, from -> {
			if (from instanceof ConsoleSender sender) return sender;
			return null;
		});
		Converters.registerConverter(Entity.class, LivingEntity.class, from -> {
			if (from instanceof LivingEntity livingEntity) return livingEntity;
			return null;
		});
		Converters.registerConverter(EquipmentHandler.class, LivingEntity.class, from -> {
			if (from instanceof LivingEntity livingEntity) return livingEntity;
			return null;
		});
		Converters.registerConverter(Entity.class, EntityCreature.class, from -> {
			if (from instanceof EntityCreature entityCreature) return entityCreature;
			return null;
		});
		Converters.registerConverter(Entity.class, Player.class, from -> {
			if (from instanceof Player player) return player;
			return null;
		});
		Converters.registerConverter(Entity.class, Pos.class, Entity::getPosition);
		Converters.registerConverter(Point.class, Pos.class, from -> {
			if (from instanceof Pos pos) return pos;
			return null;
		});
		Converters.registerConverter(Point.class, Vec.class, from -> {
			if (from instanceof Vec vec) return vec;
			return null;
		});
		Converters.registerConverter(Point.class, BlockVec.class, from -> {
			if (from instanceof BlockVec blockVec) return blockVec;
			return null;
		});
		Converters.registerConverter(Instance.class, InstanceContainer.class, from -> {
			if (from instanceof InstanceContainer container) return container;
			return null;
		});
		Converters.registerConverter(Instance.class, SharedInstance.class, from -> {
			if (from instanceof SharedInstance shared) return shared;
			return null;
		});
		Converters.registerConverter(Player.class, AbstractInventory.class, Player::getInventory);
		Converters.registerConverter(Item.class, Slot.class, from -> {
			if (from instanceof Slot slot) return slot;
			return null;
		});
		Converters.registerConverter(Slot.class, Item.class, from -> new Item(from.getItem()));

		/*
		 *	Comparators
		 */
		Comparators.registerComparator(Component.class, Component.class, (o1, o2) -> {
			LegacyComponentSerializer legacy = LegacyComponentSerializer.legacyAmpersand();
			String s1 = legacy.serialize(o1);
			String s2 = legacy.serialize(o2);
			return Comparators.compare(s1, s2);
		});

		/*
		 *	Variable Intermediaries
		 */
		Variables.registerVariableSetIntermediary(Slot.class, Item::copy);
		/*
		 *	Variable Converters
		 */
		Variables.registerVariableConverter(Player.class, player -> {
			if (SkriptConfig.enablePlayerVariableFix.value() && player.isRemoved() && player.isOnline())
				return MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(player.getUuid());
			return player;
		});
	}

	private static void inventoryChange(Object[] delta, Changer.ChangeMode mode, AbstractInventory inventory) {
		for (Object o : delta) {
			assert o != null;
			Item[] items;
			if (o instanceof Item item) items = new Item[]{item};
			else if (o instanceof AbstractInventory inv) items = Item.from(inv.getItemStacks());
			else continue; // only accepting inventories and itemstacks
			for (Item item : items) {
				if (item == null) continue;
				ItemStack internalStack = item.getItem();
				switch (mode) {
					case REMOVE_ALL -> {
						ItemStack[] stacks = inventory.getItemStacks();
						for (int i = 0; i < stacks.length; i++) {
							ItemStack itemStack = stacks[i];
							if (itemStack.isSimilar(internalStack)) stacks[i] = ItemStack.AIR;
						}
						inventory.copyContents(stacks);
					}
					case REMOVE -> {
						ItemStack[] stacks = inventory.getItemStacks();
						for (int i = 0; i < stacks.length; i++) {
							ItemStack itemStack = stacks[i];
							if (itemStack.isSimilar(internalStack)) {
								stacks[i] = itemStack.withAmount(itemStack.amount() - internalStack.amount());
								break;
							}
						}
						inventory.copyContents(stacks);
					}
					case ADD -> inventory.addItemStack(internalStack);
				}
			}
		}
	}

	private static String keyToString(Key key) {
		return key.asString().toLowerCase(Locale.ENGLISH).replace("minecraft:", "").replace('_', ' ');
	}

}
