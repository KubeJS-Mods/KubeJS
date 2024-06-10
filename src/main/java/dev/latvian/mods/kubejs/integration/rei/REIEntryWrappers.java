package dev.latvian.mods.kubejs.integration.rei;

import dev.architectury.hooks.fluid.forge.FluidStackHooksForge;
import dev.latvian.mods.kubejs.core.IngredientKJS;
import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.rhino.type.TypeInfo;
import me.shedaniel.rei.api.common.entry.type.EntryType;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public class REIEntryWrappers {
	public static class WrapperRegistryEvent extends Event {
		private final REIEntryWrappers wrappers;

		private WrapperRegistryEvent(REIEntryWrappers wrappers) {
			this.wrappers = wrappers;
		}

		public <T, C> void add(EntryType<T> type, TypeInfo typeInfo, Function<Object, C> cast, Function<C, ? extends Predicate<T>> filter, Function<C, ? extends Iterable<T>> entries) {
			wrappers.add(type, typeInfo, cast, filter, entries);
		}
	}

	private final Map<EntryType<?>, EntryWrapper<?, ?>> entryWrappers;

	public REIEntryWrappers() {
		this.entryWrappers = new HashMap<>();
		add(VanillaEntryTypes.ITEM, IngredientJS.TYPE_INFO, o -> (Ingredient) o, Function.identity(), IngredientKJS::kjs$getDisplayStacks);
		add(VanillaEntryTypes.FLUID, FluidWrapper.TYPE_INFO, o -> FluidStackHooksForge.fromForge((FluidStack) o), fs -> fs::isFluidEqual, List::of);
		NeoForge.EVENT_BUS.post(new WrapperRegistryEvent(this));
	}

	public <T, C> void add(EntryType<T> type, TypeInfo typeInfo, Function<Object, C> cast, Function<C, ? extends Predicate<T>> filter, Function<C, ? extends Iterable<T>> entries) {
		this.entryWrappers.put(type, new EntryWrapper<>(type, typeInfo, cast, filter, entries));
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public <T> EntryWrapper<T, ?> getWrapper(EntryType<T> type) {
		var wrapper = entryWrappers.get(type);

		if (wrapper == null) {
			wrapper = new EntryWrapper<>(type, TypeInfo.NONE, Function.identity(), c -> c::equals, c -> (List<T>) List.of(c));
			entryWrappers.put(type, wrapper);
		}

		return (EntryWrapper) wrapper;
	}

	public Collection<EntryWrapper<?, ?>> getWrappers() {
		return entryWrappers.values();
	}
}
