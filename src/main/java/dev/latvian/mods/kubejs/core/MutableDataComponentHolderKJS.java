package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.bindings.DataComponentWrapper;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Undefined;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Unit;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS("kjs$")
public interface MutableDataComponentHolderKJS {
	@HideFromJS
	default MutableDataComponentHolder kjs$selfHolder() {
		return (MutableDataComponentHolder) this;
	}

	@ReturnsSelf
	default MutableDataComponentHolder kjs$set(Context cx, DataComponentType<?> component, Object value) {
		var is = kjs$selfHolder();

		if (value == null || Undefined.isUndefined(value)) {
			is.remove(component);
		} else {
			is.set((DataComponentType) component, cx.jsToJava(value, DataComponentWrapper.getTypeInfo(component)));
		}

		return is;
	}

	@ReturnsSelf
	default MutableDataComponentHolder kjs$setUnit(DataComponentType<Unit> component) {
		var is = kjs$selfHolder();
		is.set(component, Unit.INSTANCE);
		return is;
	}

	@ReturnsSelf
	default MutableDataComponentHolder kjs$remove(DataComponentType<?> component) {
		var is = kjs$selfHolder();
		is.remove(component);
		return is;
	}

	@ReturnsSelf
	default MutableDataComponentHolder kjs$set(DataComponentMap components) {
		var is = kjs$selfHolder();
		is.applyComponents(components);
		return is;
	}

	@ReturnsSelf
	default MutableDataComponentHolder kjs$patch(DataComponentPatch components) {
		var is = kjs$selfHolder();
		is.applyComponents(components);
		return is;
	}

	@ReturnsSelf
	default MutableDataComponentHolder kjs$setCustomName(@Nullable Component name) {
		var is = kjs$selfHolder();

		if (name != null) {
			is.set(DataComponents.CUSTOM_NAME, name);
		} else {
			is.remove(DataComponents.CUSTOM_NAME);
		}

		return is;
	}

	@Nullable
	default Component kjs$getCustomName() {
		return kjs$selfHolder().get(DataComponents.CUSTOM_NAME);
	}
}
