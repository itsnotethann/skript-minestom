package ch.njol.skript.log;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minestom.server.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;

import static com.github.hapily04.skriptminestom.util.MessageUtils.SKRIPT_MINI_MESSAGE;

/**
 * Redirects the log to one or more {@link CommandSender}s.
 */
public class RedirectingLogHandler extends LogHandler {

	private final Collection<CommandSender> recipients;
	private int numErrors = 0;
	private final String prefix;

	public RedirectingLogHandler(CommandSender recipient, @Nullable String prefix) {
		this(Collections.singletonList(recipient), prefix);
	}

	public RedirectingLogHandler(Collection<CommandSender> recipients, @Nullable String prefix) {
		this.recipients = new ArrayList<>(recipients);
		this.prefix = prefix == null ? "" : prefix;
	}

	@Override
	public LogResult log(LogEntry entry) {
		return log(entry, null);
	}

	public LogResult log(LogEntry entry, @Nullable CommandSender ignore) {
		String formattedMessage = prefix + entry.toFormattedString();
		Component component = SKRIPT_MINI_MESSAGE.deserialize(formattedMessage);
		for (CommandSender recipient : recipients) {
			if (recipient == ignore)
				continue;
			recipient.sendMessage(component);
		}
		if (entry.level == Level.SEVERE) {
			numErrors++;
		}
		return LogResult.DO_NOT_LOG;
	}

	@Override
	public RedirectingLogHandler start() {
		return SkriptLogger.startLogHandler(this);
	}

	public int numErrors() {
		return numErrors;
	}
}