package ch.njol.skript.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Item;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.instance.block.Block;
import org.jspecify.annotations.Nullable;

public class ExprTranslationKey extends SimplePropertyExpression<Object, String> {

	static {
		register(ExprTranslationKey.class, String.class, "translation key",
			"entitytypes/items/attributetypes/blocks/sounds");
	}

	@Override
	public @Nullable String convert(Object from) {
		return switch (from) {
			case EntityType type -> type.registry().translationKey();
			case Item item -> item.getItem().material().registry().translationKey();
			case Attribute attribute -> attribute.registry().translationKey();
			case Block block -> block.registry().translationKey();
			// todo better method of getting the sound translation key
			case Sound sound -> "subtitles." + sound.name().value();
			default -> null;
		};
	}

	@Override
	protected String getPropertyName() {
		return "translation key";
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

}
