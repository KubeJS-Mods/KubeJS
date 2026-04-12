package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.component.DataComponentAccessor;
import dev.latvian.mods.rhino.util.HideFromJS;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DataComponentMap.Builder.class)
public abstract class DataComponentMapBuilderMixin implements DataComponentAccessor {
	@Shadow
	public abstract DataComponentMap build();

	@Shadow
	@Final
	private Reference2ObjectMap<DataComponentType<?>, Object> map;

	@Shadow
	@HideFromJS // replaced by kjs$set
	public abstract <T> DataComponentMap.Builder set(DataComponentType<T> component, @Nullable T value);

	@Override
	public <T> void kjs$override(DataComponentType<T> type, @Nullable T value) {
		set(type, value);
	}

	@Override
	public void kjs$remove(DataComponentType<?> type) {
		map.remove(type);
	}
}
