/**
 *   This file is part of Skript.
 *
 *  Skript is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Skript is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright Peter Güttinger, SkriptLang team and contributors
 */
package ch.njol.skript.lang;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.function.EffFunctionCall;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.log.ParseLogHandler;
import ch.njol.skript.log.SkriptLogger;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

/**
 * Supertype of conditions and effects
 *
 * @see Condition
 * @see Effect
 */
public abstract class Statement extends TriggerItem implements SyntaxElement {


	public static @Nullable Statement parse(String input, String defaultError) {
		return parse(input, null, defaultError);
	}

	public static @Nullable Statement parse(String input, @Nullable List<TriggerItem> items, String defaultError) {
		return parse(input, defaultError, null, items);
	}

	public static @Nullable Statement parse(String input, @Nullable String defaultError, @Nullable SectionNode node, @Nullable List<TriggerItem> items) {
		try (ParseLogHandler log = SkriptLogger.startParseLogHandler()) {
			Section.SectionContext sectionContext = ParserInstance.get().getData(Section.SectionContext.class);
			EffFunctionCall functionCall;
			if (node != null) {
				functionCall = sectionContext.modify(node, items, () -> {
					EffFunctionCall parsed = EffFunctionCall.parse(input);
					if (parsed != null && !sectionContext.claimed()) {
						Skript.error("The line '" + input + "' is a valid function call but cannot function as a section (:) because there is no parameter to manage it.");
						return null;
					}
					return parsed;
				});
			} else {
				functionCall = EffFunctionCall.parse(input);
			}
			if (functionCall != null) {
				log.printLog();
				return functionCall;
			} else if (log.hasError()) {
				log.printError();
				return null;
			}
			log.clear();

			EffectSection section = EffectSection.parse(input, null, node, false, items);
			if (section != null) {
				log.printLog();
				return new EffectSectionEffect(section);
			}
			log.clear();

			Statement statement;
			var iterator = Skript.instance().syntaxRegistry().syntaxes(org.skriptlang.skript.registration.SyntaxRegistry.STATEMENT).iterator();
			if (node != null) {
				var wrappedIterator = new Iterator<>() {
					@Override
					public boolean hasNext() {
						return iterator.hasNext();
					}

					@Override
					public org.skriptlang.skript.registration.SyntaxInfo<? extends Statement> next() {
						// it is possible that the section would have been claimed during the attempt to parse the previous info
						// as a result, we need to "unclaim" it
						sectionContext.owner = null;
						return iterator.next();
					}
				};
				statement = sectionContext.modify(node, items, () -> {
					//noinspection unchecked,rawtypes
					Statement parsed = (Statement) SkriptParser.parse(input, (Iterator) wrappedIterator, defaultError);
					if (parsed != null && !sectionContext.claimed()) {
						Skript.error("The line '" + input + "' is a valid statement but cannot function as a section (:) because there is no syntax in the line to manage it.");
						return null;
					}
					return parsed;
				});
			} else {
				statement = sectionContext.modify(null, null, () -> {
					//noinspection unchecked,rawtypes
					return (Statement) SkriptParser.parse(input, (Iterator) iterator, defaultError);
				});
			}

			if (statement != null) {
				log.printLog();
				return statement;
			}

			log.printError();
			return null;
		}
	}

}