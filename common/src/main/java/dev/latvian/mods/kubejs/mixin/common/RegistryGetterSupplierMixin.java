package dev.latvian.mods.kubejs.mixin.common;

import dev.latvian.mods.kubejs.core.RegistryGetterKJS;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

// FIXME: THISISTERRIBLETHISISTERRIBLETHISISTERRIBLETHISISTERRIBLETHISISTERRIBLE
@Mixin(targets = "net.minecraft.resources.RegistryReadOps$1")
public abstract class RegistryGetterSupplierMixin<E> implements RegistryGetterKJS<E> {
	@Shadow
	@Final
	Registry<E> val$registry;

	@Shadow
	@Final
	ResourceKey<E> val$elementKey;

	@Override
	public Registry<E> getRegistry() {
		return val$registry;
	}

	@Override
	public ResourceLocation getId() {
		return val$elementKey.location();
	}
}
