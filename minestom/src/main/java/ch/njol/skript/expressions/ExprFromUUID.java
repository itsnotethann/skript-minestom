package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.Validate;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExprFromUUID extends SimpleExpression<Object> {

	static {
		Skript.registerExpression(ExprFromUUID.class, Object.class, ExpressionType.PROPERTY,
			"player[s] from uuid[s] %strings%",
			"entit(y|ies) from uuid[s] %strings%",
			"instance[s] from uuid[s] %strings%");
	}

	private Expression<String> uuid;

	private int pattern;
	private Class<?> returnType;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		uuid = (Expression<String>) expressions[0];
		pattern = matchedPattern;
		returnType = getReturnType();
		return true;
	}

	@Override
	protected Object @Nullable [] get(Event event) {
		List<Object> objects = new ArrayList<>();
		for (String uuid : this.uuid.getArray(event)) {
			if (!Validate.isUUID(uuid)) continue;
			UUID u = UUID.fromString(uuid);
			Object o = switch (pattern) {
				case 0 -> MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(u);
				case 1 -> {
					for (Instance instance : MinecraftServer.getInstanceManager().getInstances()) {
						Entity entityByUuid = instance.getEntityByUuid(u);
						if (entityByUuid != null) yield entityByUuid;
					}
					yield null;
				}
				default -> MinecraftServer.getInstanceManager().getInstance(u);
			};
			if (o == null) continue;
			objects.add(o);
		}
		return objects.toArray(value -> (Object[]) Array.newInstance(returnType, value));
	}

	@Override
	public boolean isSingle() {
		return uuid.isSingle();
	}

	@Override
	public Class<?> getReturnType() {
		return switch (pattern) {
			case 0 -> Player.class;
			case 1 -> Entity.class;
			default -> Instance.class;
		};
	}

	@Override
	public String toString(@org.eclipse.jdt.annotation.Nullable Event event, boolean debug) {
		String start = switch (pattern) {
			case 0 -> "player";
			case 1 -> "entity";
			default -> "instance";
		};
		return start + " from uuid " + uuid.toString(event, debug);
	}

}
