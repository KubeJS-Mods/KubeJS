package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import dev.latvian.mods.rhino.util.SpecialEquality;
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

	@Inject(method = "<init>", at = @At(value = "RETURN"))
	private void kjs$getKeyStackTraces(ResourceLocation registryName, ResourceLocation location, CallbackInfo ci) {
		Scanner.scan(registryName, location);
	}

	@Override
	public boolean specialEquals(Context cx, Object o, boolean shallow) {
		return switch (o) {
			case ResourceKey<?> _key -> o == this;
			case ResourceLocation id -> location.equals(id);
			default -> location.toString().equals(o.toString());
		};
	}
}
