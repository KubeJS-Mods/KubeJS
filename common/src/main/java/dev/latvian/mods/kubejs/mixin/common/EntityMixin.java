package dev.latvian.mods.kubejs.mixin.common;

import dev.latvian.mods.kubejs.core.EntityKJS;
import dev.latvian.mods.rhino.mod.util.NBTUtils;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
@RemapPrefixForJS("kjs$")
public abstract class EntityMixin implements EntityKJS {
	@Unique
	private final CompoundTag kjs$persistentData = new CompoundTag();

	@Override
	public CompoundTag kjs$getPersistentData() {
		return kjs$persistentData;
	}

	@Inject(method = "saveWithoutId", at = @At("RETURN"))
	private void saveKJS(CompoundTag tag, CallbackInfoReturnable<CompoundTag> ci) {
		tag.put("KubeJSPersistentData", kjs$persistentData);
	}

	@Inject(method = "load", at = @At("RETURN"))
	private void loadKJS(CompoundTag tag, CallbackInfo ci) {
		NBTUtils.accessTagMap(kjs$persistentData).clear();
		kjs$persistentData.merge(tag.getCompound("KubeJSPersistentData"));
	}
}
