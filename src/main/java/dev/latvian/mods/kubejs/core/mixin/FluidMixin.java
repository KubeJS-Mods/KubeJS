package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.FluidKJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = Fluid.class, priority = 1001)
@RemapPrefixForJS("kjs$")
public abstract class FluidMixin implements FluidKJS {
	@Shadow
	@Final
	private Holder.Reference<Fluid> builtInRegistryHolder;

	@Unique
	private ResourceKey<Fluid> kjs$registryKey;

	@Unique
	private String kjs$id;

	@Override
	public Holder.Reference<Fluid> kjs$asHolder() {
		return builtInRegistryHolder;
	}

	@Override
	public ResourceKey<Fluid> kjs$getRegistryKey() {
		if (kjs$registryKey == null) {
			kjs$registryKey = FluidKJS.super.kjs$getRegistryKey();
		}

		return kjs$registryKey;
	}

	@Override
	public String kjs$getId() {
		if (kjs$id == null) {
			kjs$id = FluidKJS.super.kjs$getId();
		}

		return kjs$id;
	}
}
