package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.component.DataComponentWrapper;
import dev.latvian.mods.kubejs.core.component.DataComponentAccessor;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Undefined;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import org.jspecify.annotations.Nullable;

@RemapPrefixForJS("kjs$")
public interface MutableDataComponentHolderKJS extends DataComponentAccessor {
	@HideFromJS
	default MutableDataComponentHolder kjs$self() {
		throw new NoMixinException();
	}

	default DataComponentMap kjs$getComponentMap() {
		return kjs$self().getComponents();
	}

	@Override
	default <T> void kjs$override(DataComponentType<T> type, @Nullable T value) {
		if (value == null || Undefined.isUndefined(value)) {
			kjs$self().remove(type);
		} else {
			kjs$self().set(type, value);
		}
	}

	@Override
	default void kjs$set(DataComponentMap components) {
		kjs$self().applyComponents(components);
	}

	@Override
	default void kjs$patch(DataComponentPatch components) {
		kjs$self().applyComponents(components);
	}

	@Override
	default String kjs$getComponentString(Context cx) {
		if (kjs$getComponentMap() instanceof PatchedDataComponentMap map) {
			return DataComponentWrapper.patchToString(new StringBuilder(), RegistryAccessContainer.of(cx).nbt(), map.asPatch()).toString();
		}

		return "[]";
	}

	default void kjs$resetComponents(Context cx) {
		if (kjs$getComponentMap() instanceof PatchedDataComponentMap map) {
			map.restorePatch(DataComponentPatch.EMPTY);
		}
	}
}
