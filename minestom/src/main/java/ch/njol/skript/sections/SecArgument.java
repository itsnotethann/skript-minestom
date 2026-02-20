package ch.njol.skript.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.*;
import ch.njol.skript.structures.command.StructCommand;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.suggestion.Suggestion;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.eclipse.jdt.annotation.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.util.ExpressionEntryData;

import java.util.*;

@SuppressWarnings("unchecked")
public class SecArgument extends Section {

	private static final EntryValidator ENTRY_VALIDATOR = EntryValidator.builder()
		.addSection("suggestions", true)
		.addEntryData(new ExpressionEntryData<>("default value", null, true, Object.class))
		.addEntry("format", null, true)
		.addSection("trigger", true)
		.unexpectedNodeTester(node -> {
			if (node instanceof SectionNode sectionNode) {
				String key = sectionNode.getKey();
				return key == null || !key.contains("arg");
			}
			return true;
		})
		.build();

	private static final Set<String> VALID_FORMATS = Set.of("default", "lower case", "upper case");

	static {
		Skript.registerSection(SecArgument.class, "arg[ument] <.+>");
	}

	private List<SecArgument> subArguments = new ArrayList<>();
	private EntryContainer container;
	private Expression<?> defaultExpression;
	private Trigger trigger;
	@SuppressWarnings("rawtypes")
	private Argument argument;

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult,
						SectionNode sectionNode, List<TriggerItem> triggerItems) {
		container = ENTRY_VALIDATOR.validate(sectionNode);
		if (container == null) return false;
		argument = StructCommand.parseArg(StructCommand.stringToArrayDeque(parseResult.regexes.getFirst().group().replaceFirst("<", "")));
		if (argument == null) return false; // errors already made in parseArg

		String format = container.getOptional("format", String.class, false);
		if (format != null) {
			if (!(argument instanceof ArgumentEnum<?> enumArg)) {
				Skript.error("Argument can't have a format if argument type doesn't support it (gamemode, etc.).");
				return false;
			}
			if (!VALID_FORMATS.contains(format.toLowerCase(Locale.ENGLISH))) {
				Skript.error("Invalid format '" + format + "' has been provided. Valid options are default, lower case, and upper case.");
				return false;
			}
			enumArg.setFormat(ArgumentEnum.Format.valueOf(format.replace(' ', '_').toUpperCase(Locale.ENGLISH) + "D"));
		}

		SectionNode node = container.getOptional("suggestions", SectionNode.class, false);
		if (node != null) {
			Trigger trigger = loadCode(node, "argument suggestion", SuggestionCallbackEvent.class);
			argument.setSuggestionCallback((sender, context, suggestion) -> {
				TriggerItem.walk(trigger, new SuggestionCallbackEvent(sender, context, suggestion));
			});
		}

		// default expression can be unrelated to argument type rn. without reflection this may be impossible to detect
		defaultExpression = (Expression<Object>) container.getOptional("default value", false);
		if (defaultExpression != null) {
			if (LiteralUtils.hasUnparsedLiteral(defaultExpression)) defaultExpression = LiteralUtils.defendExpression(defaultExpression);
			if (!LiteralUtils.canInitSafely(defaultExpression)) {
				Skript.error("Invalid default value was provided.");
				return false;
			}
		}

		node = container.getOptional("trigger", SectionNode.class, false);
		if (node != null) trigger = loadCode(node, "argument trigger", StructCommand.CommandTriggerEvent.class);

		boolean hasSubArgs = false;
		for (Node n : container.getUnhandledNodes()) {
			Section section = StructCommand.getSection(n);
			if (!(section instanceof SecArgument secArg)) continue;
			subArguments.add(secArg);
			secArg.walk(new StructCommand.ScriptCommandEvent());
			hasSubArgs = true;
		}

		if (hasSubArgs && trigger != null) {
			Skript.error("Cannot have a trigger in this argument when it has sub arguments.");
			return false;
		} else if (!hasSubArgs && trigger == null) {
			Skript.error("If no sub arguments exist, a trigger must be present. (" + container.getSource().getKey() + ")");
			return false;
		}

		return true;
	}

	// todo pretty sure we can provide previous arg values here, but not sure how to implement it atm
	@Override
	public @Nullable TriggerItem walk(Event event) {
		if (defaultExpression != null) argument.setDefaultValue(defaultExpression.getSingle(event));
		return super.walk(event, false);
	}

	public boolean hasSubArguments() {
		return !subArguments.isEmpty();
	}

	public List<SecArgument> getSubArguments() {
		return Collections.unmodifiableList(subArguments);
	}

	public Trigger getCommandTrigger() {
		return trigger;
	}

	public Argument getArgument() {
		return argument;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "argument";
	}

	static class SuggestionCallbackEvent extends Event {

		private final CommandSender sender;
		private final CommandContext context;
		private final Suggestion suggestion;

		public SuggestionCallbackEvent(CommandSender sender, CommandContext context, Suggestion suggestion) {
			this.sender = sender;
			this.context = context;
			this.suggestion = suggestion;
		}

		public CommandSender getSender() {
			return sender;
		}

		public CommandContext getContext() {
			return context;
		}

		public Suggestion getSuggestion() {
			return suggestion;
		}

		@Override
		public HandlerList getHandlers() {
			return null;
		}

	}

}
