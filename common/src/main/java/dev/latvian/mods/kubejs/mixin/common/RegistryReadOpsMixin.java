package dev.latvian.mods.kubejs.mixin.common;

import dev.latvian.mods.kubejs.core.RegistryGetterKJS;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryReadOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(RegistryReadOps.class)
public abstract class RegistryReadOpsMixin {
	@Inject(method = "createRegistryGetter", at = @At(value = "RETURN"), cancellable = true)
	private static <E> void delegateRegistryGetter(Registry<E> registry, ResourceKey<E> resourceKey, CallbackInfoReturnable<Supplier<E>> cir) {
		var delegate = cir.getReturnValue();
		RegistryGetterKJS<E> registryGetter = new RegistryGetterKJS<>() {
			@Override
			public E get() {
				return delegate.get();
			}

			@Override
			public Registry<E> getRegistry() {
				return registry;
			}

			@Override
			public ResourceLocation getId() {
				return resourceKey.location();
			}

			@Override
			public String toString() {
				return delegate.toString();
			}
		};
		cir.setReturnValue(registryGetter);
	}
}
