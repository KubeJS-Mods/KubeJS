package dev.latvian.mods.kubejs.component;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Undefined;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS("kjs$")
@ReturnsSelf
public interface MutableDataComponentHolderFunctions extends ComponentFunctions {
	default MutableDataComponentHolder kjs$getComponentHolder() {
		return (MutableDataComponentHolder) this;
	}

	@Override
	default DataComponentMap kjs$getComponentMap() {
		return kjs$getComponentHolder().getComponents();
	}

	@Override
	@HideFromJS
	default <T> MutableDataComponentHolderFunctions kjs$override(DataComponentType<T> type, @Nullable T value) {
		var h = kjs$getComponentHolder();

		if (value == null || Undefined.isUndefined(value)) {
			h.remove(type);
		} else {
			h.set(type, value);
		}

		return this;
	}

	@Override
	default MutableDataComponentHolderFunctions kjs$set(Context cx, DataComponentMap components) {
		var h = kjs$getComponentHolder();
		h.applyComponents(components);
		return this;
	}

	@Override
	default MutableDataComponentHolderFunctions kjs$patch(Context cx, DataComponentPatch components) {
		var h = kjs$getComponentHolder();
		h.applyComponents(components);
		return this;
	}
}
