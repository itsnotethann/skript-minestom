package com.github.hapily04.skriptminestom.util;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

public class ComponentUtils {

	public static Component getComponent(@Nullable Object o) {
		if (o instanceof Component c) return c;
		if (o instanceof String s) return Component.text(s);
		return Component.empty();
	}

}
