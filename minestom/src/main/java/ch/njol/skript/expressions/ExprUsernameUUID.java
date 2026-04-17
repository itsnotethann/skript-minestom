package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.Validate;
import net.minestom.server.utils.mojang.MojangUtils;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExprUsernameUUID extends SimpleExpression<String> {

	static {
		Skript.registerExpression(ExprUsernameUUID.class, String.class, ExpressionType.COMBINED,
			"[mojang] (:uuid|username)[s] from %strings%");
	}

	private Expression<String> input;

	private boolean uuid;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		input = (Expression<String>) expressions[0];
		uuid = parseResult.hasTag("uuid");
		return true;
	}

	@Override
	protected String @Nullable [] get(Event event) {
		List<String> strings = new ArrayList<>();
		for (String s : input.getArray(event)) {
			try {
				if (!uuid) {
					if (!Validate.isUUID(s)) continue;
					strings.add(MojangUtils.getUsername(UUID.fromString(s)));
				} else {
					strings.add(MojangUtils.getUUID(s).toString());
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return strings.toArray(new String[0]);
	}

	@Override
	public boolean isSingle() {
		return input.isSingle();
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	public String toString(@org.eclipse.jdt.annotation.Nullable Event event, boolean debug) {
		return (uuid ? "uuid" : "username") + " from " + input.toString(event, debug);
	}

}
