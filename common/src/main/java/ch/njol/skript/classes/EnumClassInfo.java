package ch.njol.skript.classes;

import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.DefaultExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.util.EnumUtils;
import org.eclipse.jdt.annotation.Nullable;

/**
 * This class can be used for an easier writing of ClassInfos that are enums,
 * it registers a language node with usage, a serializer, default expression and a parser.
 * Making it easier to register enum ClassInfos.
 * @param <T> The enum class.
 */
public class EnumClassInfo<T extends Enum<T>> extends ClassInfo<T> {

	/**
	 * @param c The class
	 * @param codeName The name used in patterns
	 */
	public EnumClassInfo(Class<T> c, String codeName) {
		this(c, codeName, null);
	}

	/**
	 * @param c The class
	 * @param codeName The name used in patterns
	 * @param languageNode The language node of the type
	 */
	public EnumClassInfo(Class<T> c, String codeName, @Nullable String languageNode) {
		this(c, codeName, languageNode, new EventValueExpression<>(c));
	}

	/**
	 * @param c The class
	 * @param codeName The name used in patterns
	 * @param languageNode The language node of the type
	 * @param defaultExpression The default expression of the type
	 */
	public EnumClassInfo(Class<T> c, String codeName, @Nullable String languageNode, DefaultExpression<T> defaultExpression) {
		super(c, codeName);
		EnumUtils<T> enumUtils = languageNode != null ? new EnumUtils<>(c, languageNode) : new EnumUtils<>(c);
		usage(enumUtils.getAllNames())
			.serializer(new EnumSerializer<>(c))
			.defaultExpression(defaultExpression)
			.parser(new Parser<>() {
				@Override
				@Nullable
				public T parse(String s, ParseContext context) {
					return enumUtils.parse(s);
				}

				@Override
				public String toString(T o, int flags) {
					return enumUtils.toString(o, flags);
				}

				@Override
				public String toVariableNameString(T o) {
					return o.name();
				}
			});
	}

}
