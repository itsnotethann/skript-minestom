package ch.njol.skript.structures.command;

import ch.njol.skript.util.Item;
import net.minestom.server.command.builder.arguments.*;
import net.minestom.server.command.builder.arguments.minecraft.*;
import net.minestom.server.command.builder.arguments.minecraft.registry.ArgumentEntityType;
import net.minestom.server.command.builder.arguments.number.ArgumentDouble;
import net.minestom.server.command.builder.arguments.number.ArgumentFloat;
import net.minestom.server.command.builder.arguments.number.ArgumentInteger;
import net.minestom.server.command.builder.arguments.number.ArgumentLong;
import net.minestom.server.command.builder.arguments.relative.ArgumentRelativeBlockPosition;
import net.minestom.server.command.builder.arguments.relative.ArgumentRelativeVec2;
import net.minestom.server.command.builder.arguments.relative.ArgumentRelativeVec3;
import net.minestom.server.entity.GameMode;
import net.minestom.server.item.ItemStack;

import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

public enum ArgumentType {

	// todo ranges and more mc specific support

	// basic
	LITERAL("literal", ArgumentLiteral::new),
	BOOLEAN("boolean", ArgumentBoolean::new),
	INTEGER("integer", ArgumentInteger::new),
	LONG("long", ArgumentLong::new),
	DOUBLE("double", ArgumentDouble::new),
	FLOAT("float", ArgumentFloat::new),
	STRING("string", ArgumentString::new),
	WORD("word", ArgumentWord::new),
	STRING_ARRAY("stringarray", ArgumentStringArray::new),
	COMMAND("command", ArgumentCommand::new),

	// enums
	GAME_MODE("gamemode", GameMode.class),

	// minecraft specific
	ENTITY_TYPE("entitytype", ArgumentEntityType::new),
	BLOCK("block", ArgumentBlockState::new),
	ENTITY("entity", s -> new ArgumentEntity(s).singleEntity(true)),
	ENTITIES("entities", ArgumentEntity::new),
	PLAYER("player", s -> new ArgumentEntity(s).singleEntity(true).onlyPlayers(true)),
	PLAYERS("players", s -> new ArgumentEntity(s).onlyPlayers(true)),
	ITEM("item", ArgumentItemStack::new),
	COMPONENT("component", ArgumentComponent::new),
	UUID("uuid", ArgumentUUID::new),
	//NBT("nbt", ArgumentNbtTag::new), // todo uncomment when nbt syntax is complete
	RELATIVE_BLOCK_POSITION("blockposition", ArgumentRelativeBlockPosition::new),
	VECTOR_3("vector", ArgumentRelativeVec3::new),
	VECTOR_2("2dvector", ArgumentRelativeVec2::new);

	private final String expectedInitialInput;
	private final BiFunction<String, String, Argument<?>> provider;

	// typeInput is provided as 2nd argument so things line int ranges can use them later on when implemented
	ArgumentType(String expectedInitialInput, BiFunction<String, String, Argument<?>> provider) {
		this.expectedInitialInput = expectedInitialInput;
		this.provider = provider;
	}

	ArgumentType(String expectedInitialInput, Function<String, Argument<?>> provider) {
		this(expectedInitialInput, (s, _) -> provider.apply(s));
	}

	ArgumentType(String expectedInitialInput, Class<? extends Enum<?>> enumClass) {
		this(expectedInitialInput, s -> new ArgumentEnum<>(s, enumClass).setFormat(ArgumentEnum.Format.LOWER_CASED));
	}

	public static Object convertToSkriptObject(Object o) {
		if (o instanceof UUID uuid) return uuid.toString();
		if (o instanceof ItemStack itemStack) return new Item(itemStack);
		return o;
	}

	public boolean matchesInitialInput(String input) {
		return input.equalsIgnoreCase(expectedInitialInput);
	}

	public BiFunction<String, String, Argument<?>> getProvider() {
		return provider;
	}

}
