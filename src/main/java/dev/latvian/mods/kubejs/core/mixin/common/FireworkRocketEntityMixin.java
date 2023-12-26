package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.core.FireworkRocketEntityKJS;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketEntityMixin implements FireworkRocketEntityKJS {
	@Override
	@Accessor("lifetime")
	@Mutable
	public abstract void setLifetimeKJS(int lifetime);
}