package dev.latvian.mods.kubejs.core.mixin.forge;

import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class EntityMixin {
	@Shadow(remap = false)
	@HideFromJS
	public abstract CompoundTag getPersistentData();
}
