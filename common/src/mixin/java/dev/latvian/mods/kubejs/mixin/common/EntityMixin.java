package dev.latvian.mods.kubejs.mixin.common;

import dev.latvian.mods.kubejs.core.EntityKJS;
import dev.latvian.mods.rhino.mod.util.NBTUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityKJS {
	@Unique
	private final CompoundTag persistentDataKJS = new CompoundTag();

	@Override
	public CompoundTag getPersistentDataKJS() {
		return persistentDataKJS;
	}

	@Inject(method = "saveWithoutId", at = @At("RETURN"))
	private void saveKJS(CompoundTag tag, CallbackInfoReturnable<CompoundTag> ci) {
		tag.put("KubeJSPersistentData", persistentDataKJS);
	}

	@Inject(method = "load", at = @At("RETURN"))
	private void loadKJS(CompoundTag tag, CallbackInfo ci) {
		NBTUtils.accessTagMap(persistentDataKJS).clear();
		persistentDataKJS.merge(tag.getCompound("KubeJSPersistentData"));
	}
}
