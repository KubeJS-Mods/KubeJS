package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import dev.latvian.mods.rhino.util.SpecialEquality;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.latvian.mods.kubejs.registry.RegistryType.Scanner;

@RemapPrefixForJS("kjs$")
@Mixin(value = ResourceKey.class, priority = 1001)
public abstract class ResourceKeyMixin implements SpecialEquality {
	@Shadow
	@Final
	private ResourceLocation location;

	@Unique
	public String kjs$getNamespace() {
		return location.getNamespace();
	}

	@Unique
	public String kjs$getPath() {
		return location.getPath();
	}

	@Override
	public boolean specialEquals(Context cx, Object o, boolean shallow) {
		if (this == o) {
			return true;
		} else if (o instanceof ResourceKey) {
			return false;
		} else if (o instanceof ResourceLocation) {
			return location.equals(o);
		} else {
			return location.toString().equals(String.valueOf(o));
		}
	}

	@Inject(method = "<init>", at = @At(value = "RETURN"))
	private void kjs$getKeyStackTraces(ResourceLocation registryName, ResourceLocation location, CallbackInfo ci){
		if (Scanner.isFrozen()) return;
		if (!registryName.equals(Registries.ROOT_REGISTRY_NAME)) return;
		if (Scanner.shouldSkipNamespace(location.getNamespace())) return;
		var startTime = Util.getNanos();
		var stack = Thread.currentThread().getStackTrace();
		for (StackTraceElement stackTraceElement : stack) {
			if (Scanner.shouldSkipModule(stackTraceElement.getModuleName())) continue;
			var className = stackTraceElement.getClassName();
			if (Scanner.contains(className)) continue;
			Scanner.add(className);
		}
		KubeJS.LOGGER.debug("Took {} ms to grab stacktrace classes.", (int)((Util.getNanos() - startTime)/1_000_000));
	}
}
