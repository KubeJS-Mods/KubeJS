package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.component.ComponentFunctions;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.rhino.util.HideFromJS;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(DataComponentPatch.Builder.class)
public abstract class DataComponentPatchBuilderMixin implements ComponentFunctions {
	@Shadow
	public abstract DataComponentPatch build();

	@Shadow
	@Final
	private Reference2ObjectMap<DataComponentType<?>, Optional<?>> map;

	@Shadow
	@HideFromJS // replaced by kjs$set
	public abstract <T> DataComponentPatch.Builder set(DataComponentType<T> component, @Nullable T value);

	@Override
	public <T> @Nullable T kjs$get(DataComponentType<T> type) {
		return map.get(type).map(Cast::<T>to).orElse(null);
	}

	@Override
	public <T> ComponentFunctions kjs$override(DataComponentType<T> type, @Nullable T value) {
		if (value == null) {
			return kjs$remove(type);
		} else {
			set(type, value);
		}
		return this;
	}

	@Override
	public ComponentFunctions kjs$remove(DataComponentType<?> type) {
		map.remove(type);
		return this;
	}
}
