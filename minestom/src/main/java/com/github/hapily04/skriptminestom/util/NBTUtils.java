package com.github.hapily04.skriptminestom.util;

import net.kyori.adventure.nbt.*;
import net.minestom.server.component.DataComponent;
import net.minestom.server.component.DataComponents;
import net.minestom.server.entity.metadata.animal.SheepMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// todo make wrapper nbtcompound that has an owner (nbt compound has owner of Item/Slot, player has owner of player, etc.) that updates upon modification
// todo upon merge it'll call the right merge method (item merge may be different from entity merge, etc.)
public class NBTUtils {

	public static CompoundBinaryTag mergeItemNBT(CompoundBinaryTag originalItem, CompoundBinaryTag incomingNBT) {
		CompoundBinaryTag.Builder itemBuilder = CompoundBinaryTag.builder();
		itemBuilder.put(originalItem);
		CompoundBinaryTag.Builder customDataBuilder = CompoundBinaryTag.builder();
		String customDataKey = DataComponents.CUSTOM_DATA.name();
		CompoundBinaryTag customData = originalItem.contains(customDataKey) ? originalItem.getCompound(customDataKey) : CompoundBinaryTag.empty();
		customDataBuilder.put(customData);
		CompoundBinaryTag.Builder componentBuilder = CompoundBinaryTag.builder();
		CompoundBinaryTag components = originalItem.contains("components") ? originalItem.getCompound("components") : CompoundBinaryTag.empty();
		for (String key : incomingNBT.keySet()) {
			BinaryTag tag = incomingNBT.get(key);
			assert tag != null; // we're going through the keyset it has to have a value
			if (isItemComponentKey(key)) {
				String componentKey = key.startsWith("minecraft:") ? key : ("minecraft:" + key);
				mergeValue(componentBuilder, components, componentKey, tag);
			} else {
				mergeValue(componentBuilder, customData, key, tag);
			}
		}
		CompoundBinaryTag builtComponents = componentBuilder.build();
		itemBuilder.put("components", builtComponents);
		return itemBuilder.build();
	}

	public static CompoundBinaryTag deepMerge(CompoundBinaryTag base, CompoundBinaryTag incoming) {
		CompoundBinaryTag.Builder out = CompoundBinaryTag.builder();
		out.put(base);
		for (String key : incoming.keySet()) {
			BinaryTag in = incoming.get(key);
			if (in == null) continue;
			mergeValue(out, base, key, in);
		}
		return out.build();
	}

	private static void mergeValue(CompoundBinaryTag.Builder out, CompoundBinaryTag base, String key, BinaryTag in) {
		BinaryTag b = base.get(key);
		if (b instanceof CompoundBinaryTag bc && in instanceof CompoundBinaryTag ic) out.put(key, deepMerge(bc, ic));
		else if (b instanceof ListBinaryTag bl && in instanceof ListBinaryTag il) out.put(key, appendLists(bl, il));
		else out.put(key, in);
	}

	private static ListBinaryTag appendLists(ListBinaryTag base, ListBinaryTag incoming) {
		BinaryTagType<? extends BinaryTag> type = !base.isEmpty() ? base.elementType() : incoming.elementType();
		List<BinaryTag> all = new ArrayList<>(base.size() + incoming.size());
		for (BinaryTag e : base) {
			all.add(e);
		}
		for (BinaryTag e : incoming) {
			all.add(e);
		}
		return ListBinaryTag.listBinaryTag(type, all);
	}

	public static String asString(CompoundBinaryTag compound) throws IOException {
		return TagStringIO.tagStringIO().asString(compound);
	}

	// todo this doesn't just check item components, it checks all data components
	private static boolean isItemComponentKey(String key) {
		return DataComponent.fromKey(key.startsWith("minecraft:") ? key : ("minecraft:" + key)) != null;
	}

}
