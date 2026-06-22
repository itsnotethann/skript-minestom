package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.NBTCompound;
import ch.njol.util.Kleenean;
import com.github.hapily04.skriptminestom.util.NBTUtils;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


@Name("All NBT Tag Names")
@Description("All tag names or keys of a given type in an NBT compound.")
@Examples("set {_tags::*} to all tag names of {_nbt}")
public class ExprAllTagNames extends SimpleExpression<String> {

	static {
		Skript.registerExpression(ExprAllTagNames.class, String.class, ExpressionType.COMBINED,
			"all [%-tagtype%] tag (name|key)s of %nbtcompounds%");
	}

	@Nullable
	private Expression<NBTUtils.TagType> tagType;
	private Expression<NBTCompound> compounds;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		tagType = (Expression<NBTUtils.TagType>) expressions[0];
		compounds = (Expression<NBTCompound>) expressions[1];
		return true;
	}

	@Override
	protected String @Nullable [] get(Event event) {
		NBTUtils.TagType type = tagType == null ? null : tagType.getSingle(event);
		List<String> names = new ArrayList<>();
		for (NBTCompound compound : compounds.getArray(event)) {
			CompoundBinaryTag c = compound.getCompound();
			for (String key : c.keySet()) {
				// verify tag type of key if tag type is provided for expression
				if (type != null) {
					BinaryTag binaryTag = c.get(key);
					assert binaryTag != null;
					if (binaryTag.type() != type.getExpectedBinaryTag()) continue;
				}
				names.add(key);
			}
		}
		return names.toArray(new String[0]);
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	public String toString(@org.eclipse.jdt.annotation.Nullable Event event, boolean debug) {
		return "all " + (tagType == null ? "" : tagType.toString(event, debug) + " ") + "tag names of " + compounds.toString(event, debug);
	}

}
