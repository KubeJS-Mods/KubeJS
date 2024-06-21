package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.MutableDataComponentHolderKJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Supplier;

@Mixin(MutableDataComponentHolder.class)
public interface MutableDataComponentHolderMixin extends MutableDataComponentHolderKJS {
	@Shadow
	@HideFromJS
	<T> T set(DataComponentType<? super T> componentType, @Nullable T value);

	@Shadow
	@HideFromJS
	<T> T set(Supplier<? extends DataComponentType<? super T>> componentType, @Nullable T value);

	@Shadow
	@HideFromJS
	<T> T remove(DataComponentType<? super T> componentType);

	@Shadow
	@HideFromJS
	<T> T remove(Supplier<? extends DataComponentType<? super T>> componentType);

	@Shadow
	@HideFromJS
	void applyComponents(DataComponentPatch patch);

	@Shadow
	@HideFromJS
	void applyComponents(DataComponentMap components);
}
