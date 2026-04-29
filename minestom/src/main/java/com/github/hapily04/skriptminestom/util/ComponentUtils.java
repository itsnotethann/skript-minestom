package com.github.hapily04.skriptminestom.util;

import ch.njol.skript.util.ComponentWrapper;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

public class ComponentUtils {

	public static Component getComponent(@Nullable Object o) {
		if (o instanceof ComponentWrapper c) return c.getComponent();
		if (o instanceof String s) return Component.text(s);
		return Component.empty();
	}

}
