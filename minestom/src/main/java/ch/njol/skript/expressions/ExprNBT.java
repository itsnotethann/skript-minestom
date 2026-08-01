package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Item;
import ch.njol.skript.util.NBTCompound;
import ch.njol.util.Kleenean;
import com.github.hapily04.skriptminestom.util.FileUtils;
import com.github.hapily04.skriptminestom.util.NBTUtils;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.component.DataComponents;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.component.CustomData;
import net.minestom.server.tag.Tag;
import net.minestom.server.tag.Taggable;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


@Name("NBT Compound")
@Description("The NBT compound of a taggable, string, item, or file.")
@Examples("set {_nbt} to nbt compound of player's tool")
public class ExprNBT extends SimpleExpression<NBTCompound> {

	public static final Tag<BinaryTag> NBT_TAG = Tag.NBT("skript-minestom:custom-nbt");

	static {
		Skript.registerExpression(ExprNBT.class, NBTCompound.class, ExpressionType.COMBINED,
			"[:custom] nbt [compound[s]] (of|from) %taggables/strings/items%",
			"%taggables/strings/items%'[s] [:custom] nbt [compound[s]]",
			"nbt [compound[s]] (of|from) file[s] %strings%");
	}

	private Expression<?> holders;
	private boolean file;
	private boolean custom;

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		holders = expressions[0];
		file = matchedPattern == 2;
		custom = parseResult.hasTag("custom");
		return true;
	}

	@Override
	protected @Nullable NBTCompound[] get(Event event) {
		Object[] holders = this.holders.getArray(event);
		NBTCompound[] compounds = new NBTCompound[holders.length];
		for (int i = 0; i < holders.length; i++) {
			Object o = holders[i];
			if (!file) {
				if (o instanceof Taggable taggable) {
					CompoundBinaryTag compound;
					if (custom) compound = (CompoundBinaryTag) NBTUtils.getTagOrElse(taggable, NBT_TAG, CompoundBinaryTag.empty());
					else compound = taggable.tagHandler().asCompound();
					compounds[i] = new NBTCompound(compound, taggable, custom);
				} else if (o instanceof Item item) {
					ItemStack internalItem = item.getItem();
					CompoundBinaryTag compoundBinaryTag;
					if (custom) {
						CustomData customData = internalItem.get(DataComponents.CUSTOM_DATA);
						compoundBinaryTag = customData != null ? customData.nbt() : CompoundBinaryTag.empty();
					} else {
						CompoundBinaryTag itemNBT = internalItem.toItemNBT();
						compoundBinaryTag = itemNBT.contains("components") ? itemNBT.getCompound("components") : CompoundBinaryTag.empty();
					}
					compounds[i] = new NBTCompound(compoundBinaryTag, item, custom);
				} else if (o instanceof String s) {
					try {
						compounds[i] = new NBTCompound(NBTUtils.asCompound(s));
					} catch (Exception e) {
						Skript.error("Couldn't parse '" + s + "' as an nbt compound.");
					}
				}
			} else {
				if (o instanceof String s) {
					File f = new File(FileUtils.getServerDirectory(), s);
					try (FileInputStream input = new FileInputStream(f)) {
						compounds[i] = new NBTCompound(BinaryTagIO.reader().read(input, BinaryTagIO.Compression.GZIP));
					} catch (FileNotFoundException ignored) {
						//SkriptLogger.LOGGER.error("Couldn't find file at '{}' while attempting to create an nbt compound.", s);
					} catch (IOException ignored) {
						/*SkriptLogger.LOGGER.error("Couldn't parse file '{}' as an nbt compound:", s);
						e.printStackTrace();*/
					}
				}
			}
		}
		return compounds;
	}

	@Override
	public boolean isSingle() {
		return holders.isSingle();
	}

	@Override
	public Class<? extends NBTCompound> getReturnType() {
		return NBTCompound.class;
	}

	@Override
	public String toString(@org.eclipse.jdt.annotation.Nullable Event event, boolean debug) {
		SyntaxStringBuilder sb = new SyntaxStringBuilder(event, debug);
		if (custom) sb.append("custom");
		sb.append("nbt compound from");
		if (file) sb.append("file");
		sb.append(holders);
		return sb.toString();
	}

}
