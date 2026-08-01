package ch.njol.skript.util;

import com.github.hapily04.skriptminestom.util.NBTUtils;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.MinecraftServer;
import net.minestom.server.component.DataComponents;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.component.CustomData;
import net.minestom.server.tag.Taggable;
import org.eclipse.jdt.annotation.Nullable;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static ch.njol.skript.expressions.ExprNBT.NBT_TAG;

public class NBTCompound {

	private final AtomicReference<CompoundBinaryTag> compound;
	private final @Nullable Updater<?> updater;
	private final boolean custom;

	public NBTCompound(CompoundBinaryTag compound, @Nullable Taggable holder, boolean custom) {
		this.compound = new AtomicReference<>(compound);
		if (holder != null) updater = new TaggableUpdater(holder);
		else updater = null;
		this.custom = custom;
	}

	public NBTCompound(CompoundBinaryTag compound, Item holder, boolean custom) {
		this.compound = new AtomicReference<>(compound);
		updater = new ItemUpdater(holder);
		this.custom = custom;
	}

	public NBTCompound(CompoundBinaryTag compound, boolean custom) {
		this(compound, (Taggable) null, custom);
	}

	public CompoundBinaryTag getCompound() {
		return compound.get();
	}

	public void update(Function<CompoundBinaryTag, CompoundBinaryTag> updater) {
		CompoundBinaryTag newCompound = compound.updateAndGet(updater::apply);
		if (this.updater != null) this.updater.update(newCompound, custom);
	}

	public boolean isCustom() {
		return custom;
	}

	private abstract static class Updater<T> {

		protected final T holder;

		public Updater(T holder) {
			this.holder = holder;
		}

		abstract void update(CompoundBinaryTag newCompound, boolean custom);

	}

	private static class TaggableUpdater extends Updater<Taggable> {

		public TaggableUpdater(Taggable holder) {
			super(holder);
		}

		@Override
		void update(CompoundBinaryTag newCompound, boolean custom) {
			if (!custom) holder.tagHandler().updateContent(newCompound);
			else holder.setTag(NBT_TAG, newCompound);
		}

	}

	private static class ItemUpdater extends Updater<Item> {

		public ItemUpdater(Item holder) {
			super(holder);
		}

		@Override
		void update(CompoundBinaryTag newCompound, boolean custom) {
			holder.modify(itemStack -> {
				if (custom) return itemStack.with(DataComponents.CUSTOM_DATA, new CustomData(newCompound));
				CompoundBinaryTag.Builder componentBuilder = CompoundBinaryTag.builder();
				for (String key : newCompound.keySet()) {
					if (!NBTUtils.isItemComponentKey(key)) continue;
					BinaryTag binaryTag = newCompound.get(key);
					if (binaryTag == null) continue;
					componentBuilder.put(key, binaryTag);
				}
				return ItemStack.fromItemNBT(itemStack.toItemNBT(MinecraftServer.getRegistries()).put("components",
					componentBuilder.build()), MinecraftServer.getRegistries());
			}, true);
		}

	}

}
