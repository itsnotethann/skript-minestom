package com.github.hapily04.skriptminestom.registration;

import ch.njol.skript.SkriptConfig;
import ch.njol.skript.classes.*;
import ch.njol.skript.effects.particle.*;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.util.SimpleLiteral;
import ch.njol.skript.localization.Noun;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.*;
import ch.njol.skript.variables.Variables;
import ch.njol.util.coll.CollectionUtils;
import ch.njol.yggdrasil.Fields;
import com.github.hapily04.skriptminestom.util.NumberUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.TagStringIO;
import net.kyori.adventure.resource.ResourcePackStatus;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.util.RGBLike;
import net.minestom.server.MinecraftServer;
import net.minestom.server.color.AlphaColor;
import net.minestom.server.color.Color;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.*;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.SharedInstance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.inventory.EquipmentHandler;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.play.EntityAnimationPacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.registry.RegistryKey;
import net.minestom.server.scoreboard.Sidebar;
import net.minestom.server.tag.Taggable;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.skriptlang.skript.lang.arithmetic.Arithmetics;
import org.skriptlang.skript.lang.arithmetic.Operator;
import org.skriptlang.skript.lang.comparator.Comparators;
import org.skriptlang.skript.lang.comparator.Relation;
import org.skriptlang.skript.lang.converter.Converters;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.github.hapily04.skriptminestom.util.NumberUtils.timespanFrom;

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
		Classes.registerClass(new ClassInfo<>(CommandSender.class, "sender") // sender instead of commandsender for StructCommand
			.user("senders?")
			.name("Command Sender")
			.description("Something that can execute a command and receive messages (players/console).")
			.defaultExpression(new EventValueExpression<>(CommandSender.class)));
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
						case REMOVE_ALL, DELETE -> CollectionUtils.array(Item[].class);
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
		Classes.registerClass(new ClassInfo<>(Taggable.class, "taggable")
			.user("taggables?")
			.name("Taggable")
			.description("An object that can hold tags (entities, instances, etc.)")
			.parser(new Parser<>() {
				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return false;
				}

				@Override
				public @NotNull String toString(@NotNull Taggable o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull Taggable o) {
					return o.toString();
				} // todo better toString maybe or maybe not because this shouldn't get called
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
					return Classes.toString(o.getEntityType());
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull Entity o) {
					return o.getUuid().toString();
				}
			}));
		Classes.registerClass(new ClassInfo<>(LivingEntity.class, "livingentity")
			.user("living ?entit(y|ies)")
			.name("Living Entity")
			.description("An entity that has health, armor, and a main/offhand.")
			.defaultExpression(new EventValueExpression<>(LivingEntity.class)));
		Classes.registerClass(new ClassInfo<>(EntityCreature.class, "entitycreature")
			.user("entity ?creatures?")
			.name("Entity Creature")
			.description("An entity that has health, armor, main/offhand, and is able to pathfind.")
			.defaultExpression(new EventValueExpression<>(EntityCreature.class)));
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
					return "vector: x: " + o.x() + " y: " + o.y() + " z: " + o.z();
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
					return "instance with uuid: " + o.getUuid();
				}
			}));
		Classes.registerClass(new ClassInfo<>(InstanceContainer.class, "instancecontainer")
			.user("instance ?containers?")
			.name("Instance Container")
			.description("A world consisting of blocks and entities.")
			.defaultExpression(new EventValueExpression<>(InstanceContainer.class)));
		Classes.registerClass(new ClassInfo<>(SharedInstance.class, "sharedinstance")
			.user("shared ?instances?")
			.name("Shared Instance")
			.description("A world sharing the blocks from its underlying Instance Container. Entities are not shared.")
			.defaultExpression(new EventValueExpression<>(SharedInstance.class)));
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
					int initialBracketPos = -1;
					if (s.contains("[")) {
						if (!s.endsWith("]")) return null;
						initialBracketPos = s.indexOf('[');
						endChar = initialBracketPos-1;
					}
					else endChar = s.length()-1;
					String nameSpace = s.substring(0, endChar+1);
					nameSpace = nameSpace.replace(' ', '_');
					if (!nameSpace.contains(":")) nameSpace = "minecraft:" + nameSpace;
					else if (!nameSpace.startsWith("minecraft:")) return null; // only minecraft: is supported since you can't add mod blocks anyway atm
					if (!Key.parseable(nameSpace)) return null;
					Block block = Block.fromState(nameSpace);
					if (block == null) return null;
					if (initialBracketPos == -1) return block;
					String blockData = s.substring(initialBracketPos);
					blockData = blockData.replace("[", "").replace("]", "");
					if (blockData.isEmpty()) return block; // support stone[] (blank properties)
					int commaAmount = getCharacterAmount(blockData, ',');
					String[] properties = blockData.split(",");
					if (properties.length != commaAmount+1) return null;
					Map<String, String> propertyMap = new HashMap<>();
					Map<String, String> defaultPropertyMap = block.properties();
					for (String property : properties) {
						if (!parseProperty(property, propertyMap, defaultPropertyMap)) return null; // property did not parse
					}
					return block.withProperties(propertyMap);
				}

				private boolean parseProperty(String property, Map<String, String> into, Map<String, String> defaultProperties) {
					int equalSignAmount = getCharacterAmount(property, '=');
					if (equalSignAmount != 1) return false;
					String[] parts = property.split("=");
					String key = parts[0];
					if (!defaultProperties.containsKey(key)) return false; // invalid property for this block
					if (into.containsKey(key)) return false; // don't allow stone[bob=true,bob=false] (duplicate property keys)
					into.put(key, parts[1]);
					return true;
				}

				private int getCharacterAmount(String blockData, char character) {
					return Math.toIntExact(blockData.chars().filter(c -> c == character).count());
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
					return o.state(); // leave this because properties can be complex
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
			})
			.supplier(Block.values().toArray(new Block[0])));
		Classes.registerClass(new ClassInfo<>(GameMode.class, "gamemode")
			.user("game ?modes?")
			.name("Game Mode")
			.description("Represents a Minecraft game mode. Possible values: survival, creative, adventure, spectator.")
			.examples("set player's game mode to creative")
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
					return typeFormatted(o.name());
				}
			})
			.serializer(new EnumSerializer<>(GameMode.class))
			.supplier(GameMode.values()));
		Classes.registerClass(new ClassInfo<>(InventoryType.class, "inventorytype")
			.user("inventory ?types?")
			.name("Inventory Type")
			.description("Inventory type todo convert")
			.defaultExpression(new EventValueExpression<>(InventoryType.class))
			.parser(new Parser<>() {
				@Nullable
				public InventoryType parse(@NotNull String s, @NotNull ParseContext context) {
					s = s.toUpperCase(Locale.ENGLISH).replace(' ', '_');
					for (InventoryType type : InventoryType.values()) {
						if (type.name().equals(s)) return type;
					}
					return null;
				}

				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return true;
				}

				@Override
				public @NotNull String toString(@NotNull InventoryType o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull InventoryType o) {
					return typeFormatted(o.name());
				}
			})
			.serializer(new EnumSerializer<>(InventoryType.class))
			.supplier(InventoryType.values()));
		Classes.registerClass(new ClassInfo<>(ClickType.class, "clicktype")
			.user("click ?types?")
			.name("Click Type")
			.description("Click type todo convert")
			.defaultExpression(new EventValueExpression<>(ClickType.class))
			.parser(new Parser<>() {
				@Nullable
				public ClickType parse(@NotNull String s, @NotNull ParseContext context) {
					s = s.toUpperCase(Locale.ENGLISH);
					for (ClickType type : ClickType.values()) {
						if (type.name().equals(s)) return type;
					}
					return null;
				}

				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return true;
				}

				@Override
				public @NotNull String toString(@NotNull ClickType o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull ClickType o) {
					return typeFormatted(o.name());
				}
			})
			.serializer(new EnumSerializer<>(ClickType.class))
			.supplier(ClickType.values()));
		Classes.registerClass(new ClassInfo<>(Component.class, "component")
			.user("components?")
			.name("Component")
			.description("A piece of text with formatting (adventure component).")
			.examples("set player's tab list header to mm(\"<rainbow>Hello!\")")
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
					return LegacyComponentSerializer.legacyAmpersand().serialize(o);
				}
			})
			.serializer(new Serializer<Component>() {
				@Override
				public Fields serialize(Component o) throws NotSerializableException {
					Fields fields = new Fields();
					fields.putObject("gson", GsonComponentSerializer.gson().serialize(o));
					return fields;
				}

				@Override
				public void deserialize(Component o, Fields f) throws StreamCorruptedException, NotSerializableException {
					assert false;
				}

				@Override
				protected @NonNull Component deserialize(@NotNull Fields f) throws StreamCorruptedException {
					String componentString = f.getObject("gson", String.class);
					assert componentString != null;
					return GsonComponentSerializer.gson().deserialize(componentString);
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
		Classes.registerClass(new ClassInfo<>(TagResolver.class, "tagresolver")
			.user("tag ?resolvers?")
			.name("Tag Resolver")
			.description("Replace tags within a MiniMessage string.")
			.examples("set {_r} to resolver(\"name\", player's name)")
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
		Classes.registerClass(new ClassInfo<>(ComponentLike.class, "componentlike")
			.user("component ?likes?")
			.name("Component Like")
			.description("Represents something that can be viewed as a component, like a regular component or a hover event.")
			.examples("set {_c} to mm(\"Hello\")")
			.defaultExpression(new EventValueExpression<>(ComponentLike.class))
			.parser(new Parser<>() {
				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return false;
				}

				@Override
				public @NotNull String toString(@NotNull ComponentLike o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull ComponentLike o) {
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
					return typeFormatted(o.name());
				}
			})
			.serializer(new EnumSerializer<>(ResourcePackStatus.class)));
		Classes.registerClass(new ClassInfo<>(AbstractInventory.class, "inventory")
			.user("inventor(y|ies)")
			.name("Inventory")
			.description("Represents an inventory, such as a player's inventory or an anvil inventory.")
			.examples("open player's inventory to player")
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
						case REMOVE_ALL, DELETE -> CollectionUtils.array(Item[].class);
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
			.description("An item with its amount, enchantments and other data.")
			.examples("give player stone")
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
			.examples("spawn zombie at player")
			.defaultExpression(new EventValueExpression<>(EntityType.class))
			.parser(new Parser<>() {
				@Nullable
				public EntityType parse(@NotNull String s, @NotNull ParseContext context) {
					s = s.toLowerCase(Locale.ENGLISH).replace(' ', '_');
					s = Utils.isPlural(s).updated();
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
					return keyToString(o.key());
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
			})
			.supplier(EntityType.values().toArray(new EntityType[0])));
		Classes.registerClass(new ClassInfo<>(EquipmentSlot.class, "equipmentslot")
			.user("equipment ?slots?")
			.name("Equipment Slot")
			.description("An equipment slot for an entity. Possible values: main_hand, off_hand, boots, leggings, chestplate, helmet.")
			.examples("set helmet of player to diamond helmet")
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
					return typeFormatted(o.name()) + " slot";
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
					return "scoreboard titled \"" + LegacyComponentSerializer.legacyAmpersand().serialize(o.getTitle()) + "\"";
				}
			}));
		/*Classes.registerClass(new ClassInfo<>(Material.class, "material")
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
			}));*/
		Classes.registerClass(new ClassInfo<>(Enchantment.class, "enchantment")
			.user("enchantments?")
			.name("Enchantment")
			.description("An enchantment for an item, including its level.")
			.examples("enchant player's tool with sharpness 5")
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
		Classes.registerClass(new ClassInfo<>(Direction.class, "direction")
			.user("directions?")
			.name("Direction")
			.description("Represents a direction (north, south, east, west, up, down).")
			.examples("set {_dir} to north")
			.since("2.0")
			.defaultExpression(new SimpleLiteral<>(new Direction(new double[] {0, 0, 0}), true))
			.parser(new Parser<>() {
				@Override
				@Nullable
				public Direction parse(String s, final ParseContext context) {
					s = s.toUpperCase(Locale.ENGLISH);
					for (BlockFace blockFace : BlockFace.values()) {
						if (blockFace.name().equals(s)) return new Direction(blockFace.toDirection());
					}
					return null;
				}

				@Override
				public boolean canParse(final ParseContext context) {
					return true;
				}

				@Override
				public String toString(final Direction o, final int flags) {
					return o.toString();
				}

				@Override
				public String toVariableNameString(final Direction o) {
					return o.toString();
				}
			})
			.serializer(new YggdrasilSerializer<>()));
		Classes.registerClass(new ClassInfo<>(Sound.class, "sound")
			.user("sounds?")
			.name("Sound")
			.description("A sound with an id, seed, category, volume, and pitch.")
			.parser(new Parser<>() {
				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return false;
				}

				@Override
				public @NotNull String toString(@NotNull Sound o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull Sound o) {
					return o.toString();
				}
			})); // todo serializer
		Classes.registerClass(new ClassInfo<>(Sound.Source.class, "soundcategory")
			.user("sound ?categor(y|ies)")
			.name("Sound Category")
			.description("A sound category e.g. master")
			.parser(new Parser<>() {
				public Sound.@Nullable Source parse(@NotNull String s, @NotNull ParseContext context) {
					s = s.toUpperCase(Locale.ENGLISH).replace(' ', '_');
					for (Sound.Source source : Sound.Source.values()) {
						if (source.name().equals(s)) return source;
					}
					return null;
				}

				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return true;
				}

				@Override
				public @NotNull String toString(@NotNull Sound.Source o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull Sound.Source o) {
					return typeFormatted(o.name());
				}
			})
			.serializer(new EnumSerializer<>(Sound.Source.class))
			.supplier(Sound.Source.values()));
		Classes.registerClass(new ClassInfo<>(AbstractDisplayMeta.BillboardConstraints.class, "billboardconstraint")
			.user("bill ?board ?constraints?")
			.name("Billboard Constraints")
			.description("Billboard constraint e.g. FIXED")
			.parser(new Parser<>() {
				public AbstractDisplayMeta.BillboardConstraints parse(@NotNull String s, @NotNull ParseContext context) {
					s = s.toUpperCase(Locale.ENGLISH).replace(' ', '_');
					for (AbstractDisplayMeta.BillboardConstraints constraint : AbstractDisplayMeta.BillboardConstraints.values()) {
						if (constraint.name().equals(s)) return constraint;
					}
					return null;
				}

				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return true;
				}

				@Override
				public @NotNull String toString(@NotNull AbstractDisplayMeta.BillboardConstraints o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull AbstractDisplayMeta.BillboardConstraints o) {
					return typeFormatted(o.name());
				}
			})
			.serializer(new EnumSerializer<>(AbstractDisplayMeta.BillboardConstraints.class))
			.supplier(AbstractDisplayMeta.BillboardConstraints.values()));
		Classes.registerClass(new ClassInfo<>(NamedTextColor.class, "namedtextcolor")
			.user("named ?text ?colors?")
			.name("Named Text Color")
			.description("Team colors (dark red, dark aqua, etc.)")
			.parser(new Parser<>() {
				public NamedTextColor parse(@NotNull String s, @NotNull ParseContext context) {
					s = s.toLowerCase(Locale.ENGLISH).replace(' ', '_');
					for (NamedTextColor color : NamedTextColor.NAMES.values()) {
						if (color.toString().equals(s)) return color;
					}
					return null;
				}

				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return true;
				}

				@Override
				public @NotNull String toString(@NotNull NamedTextColor o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull NamedTextColor o) {
					return typeFormatted(o.toString());
				}
			})
			//.serializer(new EnumSerializer<>(AbstractDisplayMeta.BillboardConstraints.class)) todo too lazy to create serializer
			.supplier(NamedTextColor.NAMES.values().toArray(new NamedTextColor[0])));
		Classes.registerClass(new ClassInfo<>(Color.class, "color")
			.user("colors?")
			.name("Color")
			.description("Color (outside of the team color range)")
			.parser(new Parser<>() {
				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return false;
				}

				@Override
				public @NotNull String toString(@NotNull Color o, int flags) {
					// doesn't seem to work how I intended
					//return LegacyComponentSerializer.legacyAmpersand().serialize(Component.empty().color(TextColor.color(o.asRGB())).asComponent());
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull Color o) {
					return "color r: " + o.red() + " g: " + o.green() + " b: " + o.blue();
				}
			}));
		Classes.registerClass(new ClassInfo<>(AlphaColor.class, "alphacolor")
			.user("alpha ?colors?")
			.name("Alpha Color")
			.description("Alpha Color (color with an alpha (transparency) value)")
			.parser(new Parser<>() {
				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return false;
				}

				@Override
				public @NotNull String toString(@NotNull AlphaColor o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull AlphaColor o) {
					return "alpha color r: " + o.red() + " g: " + o.green() + " b: " + o.blue() + " alpha: " + o.alpha();
				}
			}));
		Classes.registerClass(new ClassInfo<>(ItemDisplayMeta.DisplayContext.class, "displaycontext")
			.user("display ?contexts?")
			.name("Item Display Context")
			.description("The context in which an item display is rendered (e.g. GUI)")
			.parser(new Parser<>() {
				public ItemDisplayMeta.DisplayContext parse(@NotNull String s, @NotNull ParseContext context) {
					s = s.toUpperCase(Locale.ENGLISH).replace(' ', '_');
					for (ItemDisplayMeta.DisplayContext ctx : ItemDisplayMeta.DisplayContext.values()) {
						if (ctx.name().equals(s)) return ctx;
					}
					return null;
				}

				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return true;
				}

				@Override
				public @NotNull String toString(@NotNull ItemDisplayMeta.DisplayContext o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull ItemDisplayMeta.DisplayContext o) {
					return typeFormatted(o.name());
				}
			})
			.serializer(new EnumSerializer<>(ItemDisplayMeta.DisplayContext.class))
			.supplier(ItemDisplayMeta.DisplayContext.values()));
		Classes.registerClass(new ClassInfo<>(TextDisplayMeta.Alignment.class, "textalignment")
			.user("textalignments?")
			.name("Text Alignment")
			.description("The text alignment of a text display (center, left, or right)")
			.parser(new Parser<>() {
				public TextDisplayMeta.Alignment parse(@NotNull String s, @NotNull ParseContext context) {
					s = s.toUpperCase(Locale.ENGLISH).replace(' ', '_');
					for (TextDisplayMeta.Alignment alignment : TextDisplayMeta.Alignment.values()) {
						if (alignment.name().equals(s)) return alignment;
					}
					return null;
				}

				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return true;
				}

				@Override
				public @NotNull String toString(@NotNull TextDisplayMeta.Alignment o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull TextDisplayMeta.Alignment o) {
					return typeFormatted(o.name());
				}
			})
			.serializer(new EnumSerializer<>(TextDisplayMeta.Alignment.class))
			.supplier(TextDisplayMeta.Alignment.values()));
		Classes.registerClass(new ClassInfo<>(EntityAnimationPacket.Animation.class, "animation")
			.user("animations?")
			.name("Entity Animation")
			.description("An animation that an entity can play (main hand swing, leave bed, etc.)")
			.parser(new Parser<>() {
				public EntityAnimationPacket.Animation parse(@NotNull String s, @NotNull ParseContext context) {
					s = s.toUpperCase(Locale.ENGLISH).replace(' ', '_');
					for (EntityAnimationPacket.Animation animation : EntityAnimationPacket.Animation.values()) {
						if (animation.name().equals(s)) return animation;
					}
					return null;
				}

				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return true;
				}

				@Override
				public @NotNull String toString(@NotNull EntityAnimationPacket.Animation o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull EntityAnimationPacket.Animation o) {
					return typeFormatted(o.name());
				}
			})
			.serializer(new EnumSerializer<>(EntityAnimationPacket.Animation.class))
			.supplier(EntityAnimationPacket.Animation.values()));
		Classes.registerClass(new ClassInfo<>(Particle.class, "particle")
			.user("particles?")
			.name("Particle")
			.description("Particle (e.g. dust)")
			.parser(new Parser<>() {
				public Particle parse(@NotNull String s, @NotNull ParseContext context) {
					s = s.toLowerCase(Locale.ENGLISH).replace(' ', '_');
					if (!s.contains("minecraft:")) s = "minecraft:" + s;
					if (!Key.parseable(s)) return null;
					return Particle.fromKey(s);
				}

				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return true;
				}

				@Override
				public @NotNull String toString(@NotNull Particle o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull Particle o) {
					return keyToString(o.key());
				}
			})
			.supplier(Particle.values().toArray(new Particle[0])));
		Classes.registerClass(new ClassInfo<>(DustOption.class, "dustoption")
			.user("dust ?options?")
			.name("Dust Option")
			.description("Dust options for the dust particle")
			.parser(new Parser<>() {
				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return false;
				}

				@Override
				public @NotNull String toString(@NotNull DustOption o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull DustOption o) {
					return "dust options color: " + Classes.toString(o.getColor()) + " scale: " + o.getScale();
				}
			}));
		Classes.registerClass(new ClassInfo<>(DustTransition.class, "dusttransition")
			.user("dust ?transitions?")
			.name("Dust Transition")
			.description("Dust options for the dust color transition particle")
			.parser(new Parser<>() {
				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return false;
				}

				@Override
				public @NotNull String toString(@NotNull DustTransition o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull DustTransition o) {
					return "dust transition original color: " + Classes.toString(o.getColor()) + " to color: " + Classes.toString(o.getTransitionColor())
						+ " scale: " + o.getScale();
				}
			}));
		Classes.registerClass(new ClassInfo<>(EffectData.class, "effectdata")
			.user("effect ?datas?")
			.name("Effect Data")
			.description("Effect data options for the effect and instance effect particles.")
			.parser(new Parser<>() {
				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return false;
				}

				@Override
				public @NotNull String toString(@NotNull EffectData o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull EffectData o) {
					return "effect data options color: " + Classes.toString(o.getColor()) + " power: " + o.getPower();
				}
			}));
		Classes.registerClass(new ClassInfo<>(TrailData.class, "traildata")
			.user("trail ?datas?")
			.name("Trail Data")
			.description("Trail data options for the trail particle")
			.parser(new Parser<>() {
				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return false;
				}

				@Override
				public @NotNull String toString(@NotNull TrailData o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull TrailData o) {
					return "trail data options target: " + Classes.toString(o.getTarget()) + " color: " + Classes.toString(o.getColor())
						+ " duration: " + Classes.toString(timespanFrom(o.getDuration()));
				}
			}));
		Classes.registerClass(new ClassInfo<>(VibrationData.class, "vibrationdata")
			.user("vibration ?datas?")
			.name("Vibration Data")
			.description("Vibration data options for the vibration particle")
			.parser(new Parser<>() {
				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return false;
				}

				@Override
				public @NotNull String toString(@NotNull VibrationData o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull VibrationData o) {
					return "vibration data options type: " + o.getSourceType().name().toLowerCase(Locale.ENGLISH) + " block: " + Classes.toString(o.getSourceBlock())
						+ " entity id: " + o.getSourceEntityId() + " entity eye height: " + o.getSourceEntityEyeHeight()
						+ " travel time: " + Classes.toString(timespanFrom(o.getTravelTicks()));
				}
			}));
		Classes.registerClass(new ClassInfo<>(RGBLike.class, "rgblike")
			.user("rgb ?likes?")
			.name("RGB Like (Color)")
			.description("Essentially a color")
			.parser(new Parser<>() {
				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return false;
				}

				@Override
				public @NotNull String toString(@NotNull RGBLike o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull RGBLike o) {
					return "r: " + o.red() + " g: " + o.green() + " b: " + o.blue();
				}
			}));
		Classes.registerClass(new ClassInfo<>(PlayerSkin.class, "skin")
			.user("skins?")
			.name("Skin")
			.description("A skin with textures and a signature")
			.parser(new Parser<>() {
				@Override
				public boolean canParse(@NotNull ParseContext context) {
					return false;
				}

				@Override
				public @NotNull String toString(@NotNull PlayerSkin o, int flags) {
					return toVariableNameString(o);
				}

				@Override
				public @NotNull String toVariableNameString(@NotNull PlayerSkin o) {
					return "skin textures: " + o.textures() + " signature: " + o.signature();
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
		Converters.registerConverter(Entity.class, EntityType.class, Entity::getEntityType);
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
		Converters.registerConverter(Vec.class, Direction.class, Direction::new);
		Converters.registerConverter(Direction.class, Vec.class, Direction::getDirection);
		Converters.registerConverter(Player.class, PlayerSkin.class, Player::getSkin);
		Converters.registerConverter(RGBLike.class, NamedTextColor.class, from -> {
			if (from instanceof NamedTextColor color) return color;
			return null;
		});
		Converters.registerConverter(RGBLike.class, Color.class, from -> {
			if (from instanceof Color color) return color;
			return new Color(from.red(), from.green(), from.blue());
		});
		Converters.registerConverter(Color.class, AlphaColor.class, from -> from.withAlpha(255));
		Converters.registerConverter(Item.class, Block.class, from -> from.getItem().material().block());
		Converters.registerConverter(ComponentLike.class, Component.class, from -> {
			if (from instanceof Component c) return c;
			return null;
		});

		/*
		 *	Comparators
		 */
		Comparators.registerComparator(Component.class, Component.class, (o1, o2) -> {
			LegacyComponentSerializer legacy = LegacyComponentSerializer.legacyAmpersand();
			String s1 = legacy.serialize(o1);
			String s2 = legacy.serialize(o2);
			return Comparators.compare(s1, s2);
		});
		Comparators.registerComparator(CommandSender.class, EntityType.class, (o1, o2) -> {
			if (!(o1 instanceof Player)) return Relation.get(false);
			return Relation.get(o2.equals(EntityType.PLAYER));
		});

		/*
		 *	Arithmetic
		 */
		Arithmetics.registerOperation(Operator.ADDITION, Vec.class, Vec::add);
		Arithmetics.registerOperation(Operator.SUBTRACTION, Vec.class, Vec::sub);
		Arithmetics.registerOperation(Operator.MULTIPLICATION, Vec.class, Vec::mul);
		Arithmetics.registerOperation(Operator.DIVISION, Vec.class, Vec::div);
		Arithmetics.registerOperation(Operator.ADDITION, Vec.class, Number.class, (vec, num) -> vec.add(num.doubleValue()));
		Arithmetics.registerOperation(Operator.SUBTRACTION, Vec.class, Number.class, (vec, num) -> vec.sub(num.doubleValue()));
		Arithmetics.registerOperation(Operator.MULTIPLICATION, Vec.class, Number.class, (vec, num) -> vec.mul(num.doubleValue()));
		Arithmetics.registerOperation(Operator.DIVISION, Vec.class, Number.class, (vec, num) -> vec.div(num.doubleValue()));

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
		if (mode == Changer.ChangeMode.DELETE) inventory.clear();
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
		return typeFormatted(key.asString());
	}

	private static String typeFormatted(String string) {
		return string.toLowerCase(Locale.ENGLISH).replace("minecraft:", "").replace('_', ' ');
	}


}
