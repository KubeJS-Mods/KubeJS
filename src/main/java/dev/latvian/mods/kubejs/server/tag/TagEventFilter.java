package dev.latvian.mods.kubejs.server.tag;

import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.error.EmptyTagTargetException;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.RegExpKJS;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagEntry;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public sealed interface TagEventFilter {
	static TagEventFilter of(TagKubeEvent event, Object o) {
		if (o instanceof TagEventFilter f) {
			return f;
		} else if (o instanceof Collection<?> list) {
			var filters = list.stream()
				.map(o1 -> of(event, o1))
				.flatMap(TagEventFilter::flatten)
				.filter(f -> f != Empty.INSTANCE)
				.toList();

			return filters.isEmpty() ? Empty.INSTANCE : filters.size() == 1 ? filters.getFirst() : new Or(filters);
		} else {
			var regex = RegExpKJS.wrap(o);

			if (regex != null) {
				return new RegEx(regex);
			}

			var s = o.toString().trim();

			if (!s.isEmpty()) {
				return switch (s.charAt(0)) {
					case '#' -> new Tag(event.get(Identifier.parse(s.substring(1))));
					case '@' -> new Namespace(s.substring(1));
					default -> new ID(Identifier.parse(s));
				};
			}

			return Empty.INSTANCE;
		}
	}

	static TagEventFilter unwrap(TagKubeEvent event, Object[] array) {
		return array.length == 1 ? of(event, array[0]) : of(event, Arrays.asList(array));
	}

	boolean test(Identifier id);

	default Stream<TagEventFilter> flatten() {
		return Stream.of(this);
	}

	default boolean add(TagWrapper wrapper) {
		boolean changed = false;
		var builder = wrapper.builder();

		for (var id : wrapper.event().getElementIds()) {
			if (test(id)) {
				builder.add(TagEntry.element(id));
				changed = true;
			}
		}

		return changed;
	}

	default boolean remove(TagWrapper wrapper) {
		boolean changed = false;
		var builder = wrapper.builder();

		for (var id : wrapper.event().getElementIds()) {
			if (test(id)) {
				builder.remove(TagEntry.optionalElement(id));
				changed = true;
			}
		}

		return changed;
	}

	final class Empty implements TagEventFilter {
		static final Empty INSTANCE = new Empty();

		private Empty() {
		}

		@Override
		public boolean test(Identifier id) {
			return false;
		}

		@Override
		public boolean add(TagWrapper wrapper) {
			return false;
		}

		@Override
		public boolean remove(TagWrapper wrapper) {
			return false;
		}
	}

	record Or(List<TagEventFilter> selectors) implements TagEventFilter {
		@Override
		public boolean test(Identifier id) {
			for (var selector : selectors) {
				if (selector.test(id)) {
					return true;
				}
			}

			return false;
		}

		@Override
		public Stream<TagEventFilter> flatten() {
			return selectors.stream();
		}

		@Override
		public boolean add(TagWrapper wrapper) {
			boolean changed = false;

			for (var selector : selectors) {
				changed |= selector.add(wrapper);
			}

			return changed;
		}

		@Override
		public boolean remove(TagWrapper wrapper) {
			boolean changed = false;

			for (var selector : selectors) {
				changed |= selector.remove(wrapper);
			}

			return changed;
		}
	}

	record ID(Identifier id) implements TagEventFilter {
		@Override
		public boolean test(Identifier id) {
			return this.id.equals(id);
		}

		@Override
		public boolean add(TagWrapper wrapper) {
			if (wrapper.event().hasElement(id)) {
				wrapper.builder().add(TagEntry.element(id));
				return true;
			}

			var msg = "No such element %s in registry %s".formatted(id, wrapper.event().registryKey.identifier());

			if (DevProperties.get().strictTags) {
				throw new EmptyTagTargetException(msg);
			} else if (DevProperties.get().logSkippedTags) {
				ScriptType.SERVER.console.warn(msg);
			}

			return false;
		}

		@Override
		public boolean remove(TagWrapper wrapper) {
			if (wrapper.event().hasElement(id)) {
				wrapper.builder().remove(TagEntry.element(id));
				return true;
			}

			var msg = "No such element %s in registry %s".formatted(id, wrapper.event().registryKey.identifier());

			if (DevProperties.get().strictTags) {
				throw new EmptyTagTargetException(msg);
			} else if (DevProperties.get().logSkippedTags) {
				ScriptType.SERVER.console.warn(msg);
			}

			return false;
		}
	}

	record Tag(TagWrapper tag) implements TagEventFilter {
		@Override
		public boolean test(Identifier id) {
			return false;
		}

		@Override
		public boolean add(TagWrapper wrapper) {
			wrapper.builder().add(TagEntry.tag(tag.id()));
			return true;
		}

		@Override
		public boolean remove(TagWrapper wrapper) {
			wrapper.builder().remove(TagEntry.optionalTag(tag.id()));
			return true;
		}
	}

	record Namespace(String namespace) implements TagEventFilter {
		@Override
		public boolean test(Identifier id) {
			return id.getNamespace().equals(namespace);
		}
	}

	record RegEx(Pattern pattern) implements TagEventFilter {
		@Override
		public boolean test(Identifier id) {
			return pattern.matcher(id.toString()).find();
		}
	}
}
