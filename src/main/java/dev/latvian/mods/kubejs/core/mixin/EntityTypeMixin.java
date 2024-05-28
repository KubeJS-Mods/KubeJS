package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.EntityTypeKJS;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityType.class)
public abstract class EntityTypeMixin implements EntityTypeKJS {
}
