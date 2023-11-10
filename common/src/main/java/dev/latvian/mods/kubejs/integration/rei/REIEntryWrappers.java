package dev.latvian.mods.kubejs.integration.rei;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.latvian.mods.kubejs.core.IngredientKJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import me.shedaniel.rei.api.common.entry.type.EntryType;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class REIEntryWrappers {
	public static final Event<Consumer<REIEntryWrappers>> EVENT = EventFactory.createConsumerLoop();

	private final Map<EntryType<?>, EntryWrapper<?, ?>> entryWrappers;

	public REIEntryWrappers() {
		this.entryWrappers = new HashMap<>();
		add(VanillaEntryTypes.ITEM, IngredientJS::of, Function.identity(), IngredientKJS::kjs$getDisplayStacks);
		// add(VanillaEntryTypes.FLUID, o -> FluidStackJS.of(o));
		EVENT.invoker().accept(this);
	}

	public <T, C> void add(EntryType<T> type, Function<Object, C> converter, Function<C, ? extends Predicate<T>> filter, Function<C, ? extends Iterable<T>> entries) {
		this.entryWrappers.put(type, new EntryWrapper<>(type, converter, filter, entries));
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public <T> EntryWrapper<T, ?> getWrapper(EntryType<T> type) {
		var wrapper = entryWrappers.get(type);

		if (wrapper == null) {
			wrapper = new EntryWrapper<>(type, Function.identity(), c -> c::equals, c -> (List<T>) List.of(c));
			entryWrappers.put(type, wrapper);
		}

		return (EntryWrapper) wrapper;
	}

	public Collection<EntryWrapper<?, ?>> getWrappers() {
		return entryWrappers.values();
	}
}
