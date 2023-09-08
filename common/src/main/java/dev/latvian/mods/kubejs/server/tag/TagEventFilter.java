package dev.latvian.mods.kubejs.server.tag;

import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.ExtraCodecs;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public interface TagEventFilter {
	static TagEventFilter of(TagEventJS event, Object o) {
		if (o instanceof TagEventFilter f) {
			return f;
		} else if (o instanceof Collection<?> list) {
			var filters = list.stream()
				.map(o1 -> of(event, o1))
				.flatMap(TagEventFilter::unwrap)
				.filter(f -> f != Empty.INSTANCE)
				.toList();

			return filters.isEmpty() ? Empty.INSTANCE : filters.size() == 1 ? filters.get(0) : new Or(filters);
		} else {
			var regex = UtilsJS.parseRegex(o);

			if (regex != null) {
				return new RegEx(regex);
			}

			var s = o.toString().trim();

			if (!s.isEmpty()) {
				return switch (s.charAt(0)) {
					case '#' -> new Tag(event.get(new ResourceLocation(s.substring(1))));
					case '@' -> new Namespace(s.substring(1));
					default -> new ID(new ResourceLocation(s));
				};
			}

			return Empty.INSTANCE;
		}
	}

	static TagEventFilter unwrap(TagEventJS event, Object[] array) {
		var filter = array.length == 1 ? of(event, array[0]) : of(event, Arrays.asList(array));

		/*
		if (filter.isEmpty()) {
			var msg = "No matches found for filter %s!".formatted(filter);

			if (DevProperties.get().strictTags) {
				throw new EmptyTagTargetException(msg);
			} else if (DevProperties.get().logSkippedTags) {
				ConsoleJS.SERVER.warn(msg);
			}
		}
		 */

		return filter;
	}

	boolean testElementId(ResourceLocation id);

	default boolean testTagOrElementLocation(ExtraCodecs.TagOrElementLocation element) {
		return !element.tag() && testElementId(element.id());
	}

	default Stream<TagEventFilter> unwrap() {
		return Stream.of(this);
	}

	default int add(TagWrapper wrapper) {
		int count = 0;

		for (var id : wrapper.event.getElementIds()) {
			if (testElementId(id)) {
				wrapper.entries.add(new TagLoader.EntryWithSource(TagEntry.element(id), TagEventJS.SOURCE));
				count++;
			}
		}

		return count;
	}

	default int remove(TagWrapper wrapper) {
		int count = 0;
		var itr = wrapper.entries.iterator();

		while (itr.hasNext()) {
			var it = itr.next();

			if (!it.entry().tag && testElementId(it.entry().id)) {
				itr.remove();
				count++;
			}
		}

		return count;
	}

	class Empty implements TagEventFilter {
		public static final Empty INSTANCE = new Empty();

		@Override
		public boolean testElementId(ResourceLocation resourceLocation) {
			return false;
		}

		@Override
		public boolean testTagOrElementLocation(ExtraCodecs.TagOrElementLocation element) {
			return false;
		}

		@Override
		public int add(TagWrapper wrapper) {
			return 0;
		}

		@Override
		public int remove(TagWrapper wrapper) {
			return 0;
		}
	}

	record Or(List<TagEventFilter> filters) implements TagEventFilter {
		@Override
		public boolean testElementId(ResourceLocation resourceLocation) {
			for (var filter : filters) {
				if (filter.testElementId(resourceLocation)) {
					return true;
				}
			}

			return false;
		}

		@Override
		public boolean testTagOrElementLocation(ExtraCodecs.TagOrElementLocation element) {
			for (var filter : filters) {
				if (filter.testTagOrElementLocation(element)) {
					return true;
				}
			}

			return false;
		}

		@Override
		public Stream<TagEventFilter> unwrap() {
			return filters.stream();
		}

		@Override
		public int add(TagWrapper wrapper) {
			int count = 0;

			for (var filter : filters) {
				count += filter.add(wrapper);
			}

			return count;
		}

		@Override
		public int remove(TagWrapper wrapper) {
			int count = 0;

			for (var filter : filters) {
				count += filter.remove(wrapper);
			}

			return count;
		}
	}

	record ID(ResourceLocation id) implements TagEventFilter {
		@Override
		public boolean testElementId(ResourceLocation id) {
			return this.id.equals(id);
		}

		@Override
		public int add(TagWrapper wrapper) {
			if (wrapper.event.getElementIds().contains(id)) {
				wrapper.entries.add(new TagLoader.EntryWithSource(TagEntry.element(id), TagEventJS.SOURCE));
				return 1;
			} else {
				var msg = "No such element %s in registry %s".formatted(id, wrapper.event.registry);

				if (DevProperties.get().strictTags) {
					throw new EmptyTagTargetException(msg);
				} else if (DevProperties.get().logSkippedTags) {
					ConsoleJS.SERVER.warn(msg);
				}

				return 0;
			}
		}
	}

	record Tag(TagWrapper tag) implements TagEventFilter {
		@Override
		public boolean testElementId(ResourceLocation id) {
			return false;
		}

		@Override
		public boolean testTagOrElementLocation(ExtraCodecs.TagOrElementLocation element) {
			return element.tag() && this.tag.id.equals(element.id());
		}

		@Override
		public int add(TagWrapper wrapper) {
			wrapper.entries.add(new TagLoader.EntryWithSource(TagEntry.tag(tag.id), TagEventJS.SOURCE));
			return 1;
		}

		@Override
		public int remove(TagWrapper wrapper) {
			int count = 0;
			var itr = wrapper.entries.iterator();

			while (itr.hasNext()) {
				var it = itr.next();

				if (it.entry().tag && it.entry().id.equals(tag.id)) {
					itr.remove();
					count++;
				}
			}

			return count;
		}
	}

	record Namespace(String namespace) implements TagEventFilter {
		@Override
		public boolean testElementId(ResourceLocation id) {
			return id.getNamespace().equals(namespace);
		}
	}

	record RegEx(Pattern pattern) implements TagEventFilter {
		@Override
		public boolean testElementId(ResourceLocation id) {
			return pattern.matcher(id.toString()).find();
		}
	}
}
