package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.EventRestrictedSyntax;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.sections.SecArgument;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import net.minestom.server.command.builder.suggestion.Suggestion;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;


@Name("Argument Suggestions")
@Description("The suggestion entries in an argument section callback.")
@Examples("set {_suggestions::*} to argument suggestions")
public class ExprArgumentSuggestions extends SimpleExpression<SuggestionEntry> implements EventRestrictedSyntax {

	static {
		Skript.registerExpression(ExprArgumentSuggestions.class, SuggestionEntry.class, ExpressionType.SIMPLE, "[arg[ument]] suggestions");
	}

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		return true;
	}

	@Override
	protected @Nullable SuggestionEntry[] get(Event event) {
		return ((SecArgument.SuggestionCallbackEvent) event).getSuggestion().getEntries().toArray(new SuggestionEntry[0]);
	}

	@Override
	public @org.eclipse.jdt.annotation.Nullable Class<?>[] acceptChange(Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.ADD) return CollectionUtils.array(String[].class, SuggestionEntry[].class);
		return null;
	}

	@SuppressWarnings("ConstantValue")
	@Override
	public void change(Event event, @Nullable @org.eclipse.jdt.annotation.Nullable Object[] delta, Changer.ChangeMode mode) {
		Suggestion suggestion = ((SecArgument.SuggestionCallbackEvent) event).getSuggestion();
		for (Object o : delta) {
			if (o instanceof String s) suggestion.addEntry(new SuggestionEntry(s));
			else if (o instanceof SuggestionEntry entry) suggestion.addEntry(entry);
		}
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@Override
	public Class<? extends SuggestionEntry> getReturnType() {
		return SuggestionEntry.class;
	}

	@Override
	public String toString(@org.eclipse.jdt.annotation.Nullable Event event, boolean debug) {
		return "argument suggestions";
	}

	@Override
	public Class<? extends Event>[] supportedEvents() {
		return new Class[]{SecArgument.SuggestionCallbackEvent.class};
	}

}
