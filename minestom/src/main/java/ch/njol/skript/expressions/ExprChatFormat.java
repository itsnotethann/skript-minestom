package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.events.wrapper.PlayerChatWrapper;
import ch.njol.skript.lang.EventRestrictedSyntax;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.ComponentWrapper;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;

import static ch.njol.skript.util.ComponentWrapper.toWrapper;

@Name("Chat Format")
@Description("The format of a chat message in a chat event.")
@Examples("set chat format to \"[Admin] %player%: %message%\"")
public class ExprChatFormat extends SimpleExpression<ComponentWrapper> implements EventRestrictedSyntax {

	static {
		Skript.registerExpression(ExprChatFormat.class, ComponentWrapper.class, ExpressionType.SIMPLE, "chat format");
	}

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		return true;
	}

	@Override
	protected @Nullable ComponentWrapper[] get(Event event) {
		return new ComponentWrapper[]{toWrapper(((PlayerChatWrapper) event).getEvent().getFormattedMessage())};
	}

	@Override
	public @org.eclipse.jdt.annotation.Nullable Class<?>[] acceptChange(Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET) return CollectionUtils.array(ComponentWrapper.class);
		return null;
	}

	@Override
	public void change(Event event, @Nullable @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) {
		ComponentWrapper format = (ComponentWrapper) delta[0];
		if (format == null) return;
		((PlayerChatWrapper) event).getEvent().setFormattedMessage(format.getComponent());
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends ComponentWrapper> getReturnType() {
		return ComponentWrapper.class;
	}

	@Override
	public String toString(@org.eclipse.jdt.annotation.Nullable Event event, boolean debug) {
		return "chat format";
	}

	@Override
	public Class<? extends Event>[] supportedEvents() {
		return new Class[]{PlayerChatWrapper.class};
	}

}
