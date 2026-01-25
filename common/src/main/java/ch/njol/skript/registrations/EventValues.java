package ch.njol.skript.registrations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import ch.njol.skript.Skript;
import org.skriptlang.skript.lang.converter.Converter;
import org.skriptlang.skript.lang.converter.Converters;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.util.Getter;
import ch.njol.util.Kleenean;

public class EventValues {

	private EventValues() {}

	private static final class EventValueInfo<T> {

		public final Class<?> event;
		public final Class<T> c;
		@SuppressWarnings("rawtypes")
		public final Getter getter;
		@Nullable
		public final Class<? extends Event>[] excludes;
		@Nullable
		public final String excludeErrorMessage;

		@SuppressWarnings("rawtypes")
		public EventValueInfo(Class<?> event, Class<T> c, Getter getter, @Nullable String excludeErrorMessage, @Nullable Class<? extends Event>[] excludes) {
			assert event != null;
			assert c != null;
			assert getter != null;
			this.event = event;
			this.c = c;
			this.getter = getter;
			this.excludes = excludes;
			this.excludeErrorMessage = excludeErrorMessage;
		}

		public Class<?> getEventClass() {
			return event;
		}

		public Class<T> getValueClass() {
			return c;
		}

		@Nullable
		public Class<? extends Event>[] getExcludes() {
			if (excludes != null)
				return Arrays.copyOf(excludes, excludes.length);
			return new Class[0];
		}

		@Nullable
		public String getExcludeErrorMessage() {
			return excludeErrorMessage;
		}
	}

	private static final List<EventValueInfo<?>> defaultEventValues = new ArrayList<>(30);
	private static final List<EventValueInfo<?>> futureEventValues = new ArrayList<>();
	private static final List<EventValueInfo<?>> pastEventValues = new ArrayList<>();

	public static final int TIME_PAST = -1;
	public static final int TIME_NOW = 0;
	public static final int TIME_FUTURE = 1;

	public static List<EventValueInfo<?>> getEventValuesListForTime(int time) {
		return ImmutableList.copyOf(getEventValuesList(time));
	}

	private static List<EventValueInfo<?>> getEventValuesList(int time) {
		if (time == -1)
			return pastEventValues;
		if (time == 0)
			return defaultEventValues;
		if (time == 1)
			return futureEventValues;
		throw new IllegalArgumentException("time must be -1, 0, or 1");
	}

	public static <T, E extends Event> void registerEventValue(Class<E> event, Class<T> type, Getter<T, E> getter, int time) {
		registerEventValue(event, type, getter, time, null, (Class<? extends E>[]) null);
	}

	@SafeVarargs
	public static <T, E extends Event> void registerEventValue(Class<E> event, Class<T> type, Getter<T, E> getter, int time, @Nullable String excludeErrorMessage, @Nullable Class<? extends E>... excludes) {
		Skript.checkAcceptRegistrations();
		List<EventValueInfo<?>> eventValues = getEventValuesList(time);
		for (int i = 0; i < eventValues.size(); i++) {
			EventValueInfo<?> info = eventValues.get(i);
			if (info.event.equals(event) && info.c.equals(type))
				return;
			if (!info.event.equals(event) ? info.event.isAssignableFrom(event) : info.c.isAssignableFrom(type)) {
				eventValues.add(i, new EventValueInfo<>(event, type, getter, excludeErrorMessage, (Class<? extends Event>[]) excludes));
				return;
			}
		}
		eventValues.add(new EventValueInfo<>(event, type, getter, excludeErrorMessage, (Class<? extends Event>[]) excludes));
	}

	public static <T, M> void registerEventValueMarker(Class<M> marker, Class<T> type, Getter<T, M> getter, int time) {
		registerEventValueMarker(marker, type, getter, time, null, (Class<? extends Event>[]) null);
	}

	@SafeVarargs
	public static <T, M> void registerEventValueMarker(Class<M> marker, Class<T> type, Getter<T, M> getter, int time, @Nullable String excludeErrorMessage, @Nullable Class<? extends Event>... excludes) {
		Skript.checkAcceptRegistrations();
		List<EventValueInfo<?>> eventValues = getEventValuesList(time);
		for (int i = 0; i < eventValues.size(); i++) {
			EventValueInfo<?> info = eventValues.get(i);
			if (info.event.equals(marker) && info.c.equals(type))
				return;
			if (!info.event.equals(marker) ? info.event.isAssignableFrom(marker) : info.c.isAssignableFrom(type)) {
				eventValues.add(i, new EventValueInfo<>(marker, type, getter, excludeErrorMessage, excludes));
				return;
			}
		}
		eventValues.add(new EventValueInfo<>(marker, type, getter, excludeErrorMessage, excludes));
	}

	@Nullable
	public static <T, E extends Event> T getEventValue(E e, Class<T> c, int time) {
		@SuppressWarnings("unchecked")
		Getter<? extends T, ? super E> getter = getEventValueGetter((Class<E>) e.getClass(), c, time);
		if (getter == null)
			return null;
		return getter.get(e);
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public static <T, E extends Event> Getter<? extends T, ? super E> getExactEventValueGetter(Class<E> event, Class<T> c, int time) {
		List<EventValueInfo<?>> eventValues = getEventValuesList(time);
		for (EventValueInfo<?> info : eventValues) {
			if (!c.equals(info.c))
				continue;
			if (!checkExcludes(info, event))
				return null;
			if (info.event.isAssignableFrom(event))
				return (Getter<? extends T, ? super E>) info.getter;
		}
		return null;
	}

	public static <T, E extends Event> Kleenean hasMultipleGetters(Class<E> event, Class<T> type, int time) {
		List<Getter<? extends T, ? super E>> getters = getEventValueGetters(event, type, time, true, false);
		if (getters == null)
			return Kleenean.UNKNOWN;
		return Kleenean.get(getters.size() > 1);
	}

	@Nullable
	public static <T, E extends Event> Getter<? extends T, ? super E> getEventValueGetter(Class<E> event, Class<T> type, int time) {
		return getEventValueGetter(event, type, time, true);
	}

	@Nullable
	private static <T, E extends Event> Getter<? extends T, ? super E> getEventValueGetter(Class<E> event, Class<T> type, int time, boolean allowDefault) {
		List<Getter<? extends T, ? super E>> list = getEventValueGetters(event, type, time, allowDefault);
		if (list == null || list.isEmpty())
			return null;
		return list.get(0);
	}

	@Nullable
	private static <T, E extends Event> List<Getter<? extends T, ? super E>> getEventValueGetters(Class<E> event, Class<T> type, int time, boolean allowDefault) {
		return getEventValueGetters(event, type, time, allowDefault, true);
	}

	@Nullable
	@SuppressWarnings("unchecked")
	private static <T, E extends Event> List<Getter<? extends T, ? super E>> getEventValueGetters(Class<E> event, Class<T> type, int time, boolean allowDefault, boolean allowConverting) {
		List<EventValueInfo<?>> eventValues = getEventValuesList(time);
		List<Getter<? extends T, ? super E>> list = new ArrayList<>();

		Getter<? extends T, ? super E> exact = (Getter<? extends T, ? super E>) getExactEventValueGetter(event, type, time);
		if (exact != null) {
			list.add(exact);
			return list;
		}

		for (EventValueInfo<?> info : eventValues) {
			if (!type.isAssignableFrom(info.c))
				continue;
			if (!checkExcludes(info, event))
				return null;
			if (info.event.isAssignableFrom(event)) {
				list.add((Getter<? extends T, ? super E>) info.getter);
				continue;
			}
			if (!(Event.class.isAssignableFrom(info.event)))
				continue;
			if (!event.isAssignableFrom((Class<? extends Event>) info.event))
				continue;
			Class<?> key = info.event;
			list.add(new Getter<T, E>() {
				@Override
				@Nullable
				public T get(E ev) {
					if (!key.isInstance(ev))
						return null;
					return ((Getter<? extends T, E>) info.getter).get(ev);
				}
			});
		}

		if (!list.isEmpty())
			return list;
		if (!allowConverting)
			return null;

		for (EventValueInfo<?> info : eventValues) {
			if (!info.c.isAssignableFrom(type))
				continue;
			boolean checkInstanceOf = !info.event.isAssignableFrom(event);
			if (checkInstanceOf && Event.class.isAssignableFrom(info.event)) {
				if (!event.isAssignableFrom((Class<? extends Event>) info.event))
					continue;
			} else if (checkInstanceOf) {
				continue;
			}
			if (!checkExcludes(info, event))
				return null;
			Class<?> key = info.event;
			list.add(new Getter<T, E>() {
				@Override
				@Nullable
				public T get(E ev) {
					if (checkInstanceOf && !key.isInstance(ev))
						return null;
					Object object = ((Getter<? super T, ? super E>) info.getter).get(ev);
					if (type.isInstance(object))
						return (T) object;
					return null;
				}
			});
		}

		if (!list.isEmpty())
			return list;

		for (EventValueInfo<?> info : eventValues) {
			if (!event.equals(info.event))
				continue;
			Getter<? extends T, ? super E> getter = (Getter<? extends T, ? super E>) getConvertedGetter(info, type, false);
			if (getter == null)
				continue;
			if (!checkExcludes(info, event))
				return null;
			list.add(getter);
		}

		if (!list.isEmpty())
			return list;

		for (EventValueInfo<?> info : eventValues) {
			if (!(Event.class.isAssignableFrom(info.event)))
				continue;
			if (!event.isAssignableFrom((Class<? extends Event>) info.event))
				continue;
			Getter<? extends T, ? super E> getter = (Getter<? extends T, ? super E>) getConvertedGetter(info, type, true);
			if (getter == null)
				continue;
			if (!checkExcludes(info, event))
				return null;
			list.add(getter);
		}

		if (!list.isEmpty())
			return list;

		if (allowDefault && time != 0)
			return getEventValueGetters(event, type, 0, false);
		return null;
	}

	private static boolean checkExcludes(EventValueInfo<?> info, Class<? extends Event> event) {
		if (info.excludes == null)
			return true;
		for (Class<? extends Event> ex : info.excludes) {
			if (ex.isAssignableFrom(event)) {
				Skript.error(info.excludeErrorMessage);
				return false;
			}
		}
		return true;
	}

	@Nullable
	@SuppressWarnings("unchecked")
	private static <E extends Event, F, T> Getter<? extends T, ? super E> getConvertedGetter(EventValueInfo<F> info, Class<T> to, boolean checkInstanceOf) {
		Converter<? super F, ? extends T> converter = Converters.getConverter(info.c, to);
		if (converter == null)
			return null;
		Class<?> key = info.event;
		return new Getter<T, E>() {
			@Override
			@Nullable
			public T get(E e) {
				if (checkInstanceOf && !key.isInstance(e))
					return null;
				F f = ((Getter<F, E>) info.getter).get(e);
				if (f == null)
					return null;
				return converter.convert(f);
			}
		};
	}

	public static boolean doesExactEventValueHaveTimeStates(Class<? extends Event> event, Class<?> c) {
		return getExactEventValueGetter(event, c, TIME_PAST) != null || getExactEventValueGetter(event, c, TIME_FUTURE) != null;
	}

	public static boolean doesEventValueHaveTimeStates(Class<? extends Event> event, Class<?> c) {
		return getEventValueGetter(event, c, TIME_PAST, false) != null || getEventValueGetter(event, c, TIME_FUTURE, false) != null;
	}
}
