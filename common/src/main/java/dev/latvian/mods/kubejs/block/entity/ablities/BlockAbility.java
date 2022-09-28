package dev.latvian.mods.kubejs.block.entity.ablities;

import dev.latvian.mods.kubejs.block.entity.ablities.wrappers.AbilityTypeWrapper;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.NativeObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("StaticInitializerReferencesSubClass")
public abstract class BlockAbility<T> {
	public static final HashMap<String, Function<AbilityJS, BlockAbility<?>>> registry = new HashMap<>();

	static {
		BlockAbility.registry.put("energy", EnergyBlockAbility::new);
		BlockAbility.registry.put("fluid", FluidBlockAbility::new);
		BlockAbility.registry.put("item", ItemBlockAbility::new);
	}

	public record SlotDefinition(String id, Number limit, boolean input, boolean output) {

		public static SlotDefinition of(Object o) {
			if (o instanceof NativeObject obj) {
				return new SlotDefinition(
						obj.get("id").toString(),
						UtilsJS.parseLong(obj.get("limit"), 1),
						UtilsJS.cast(obj.get("input")),
						UtilsJS.cast(obj.get("output"))
				);
			}
			return null;
		}
	}

	public record AbilityJS(String type, List<SlotDefinition> slots) {
		public static AbilityJS of(Object o) {
			if (o instanceof NativeObject obj) {
				return new AbilityJS(obj.get("type").toString(), UtilsJS.cast(ListJS.orSelf(obj.get("slots"))));
			}
			return null;
		}
	}

	public BlockAbility(AbilityJS map) {

	}

	public abstract Map<String, AbilityTypeWrapper<T>> getSlotMap();

	public Set<String> getSlots() {
		return getSlotMap().keySet();
	}

	public int getSlotsLength() {
		return getSlotMap().size();
	}

	public AbilityTypeWrapper<T> get(String slot) {
		return getSlotMap().get(slot);
	}

	public void set(String slot, T object) {
		getSlotMap().get(slot).setRaw(object);
	}

	public long getMax(String slot) {
		return getSlotMap().get(slot).getMax();
	}

	public T grow(String slot, int num, boolean simulate) {
		return insert(slot, get(slot).withCount(num).getRaw(), simulate);
	}

	public T shrink(String slot, int num, boolean simulate) {
		return extract(slot, num, simulate);
	}

	public T insert(String slot, T object, boolean simulate) {
		var slotWrapper = get(slot);
		if (slotWrapper.compatible(object)) {
			if (slotWrapper.isEmpty()) {
				var empty = slotWrapper.copy();
				slotWrapper.setRaw(object);
				return empty.getRaw();
			} else {
				var copy = slotWrapper.withRaw(object);
				var remainder = copy.withCount(slotWrapper.grow(copy.getCount(), simulate));
				return remainder.getRaw();
			}
		}
		return object;
	}

	public boolean canInsert(String slot, T object) {
		var slotWrapper = get(slot);
		return slotWrapper.compatible(object);
	}

	public T extract(String slot, int amount, boolean simulate) {
		var slotWrapper = get(slot);
		var remainder = slotWrapper.shrink(amount, simulate);
		return slotWrapper.withCount(remainder).getRaw();
	}


	public abstract void markDirty(String slot);

	public void markDirty() {
		for (var key : getSlots()) {
			markDirty(key);
		}
	}

	public void forEach(BiConsumer<AbilityTypeWrapper<T>, String> cb) {
		for (var key : getSlots()) {
			cb.accept(get(key), key);
		}
	}

	public void map(BiFunction<AbilityTypeWrapper<T>, String, AbilityTypeWrapper<T>> cb) {
		for (var key : getSlots()) {
			set(key, cb.apply(get(key), key).getRaw());
		}
	}

	public abstract void onChanged(BiConsumer<String, AbilityTypeWrapper<T>> cb);

	public abstract void onSlotChanged(String slot, Consumer<AbilityTypeWrapper<T>> cb);

	public Tag toTag() {
		CompoundTag nbt = new CompoundTag();
		for (var key : getSlots()) {
			nbt.put(key, get(key).toTag());
		}
		return nbt;
	}

	public void fromTag(Tag tag) {
		CompoundTag nbt = (CompoundTag) (tag);
		for (var key : nbt.getAllKeys()) {
			get(key).fromTag(nbt.get(key));
		}
	}
}
