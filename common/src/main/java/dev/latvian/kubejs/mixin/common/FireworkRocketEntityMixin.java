package dev.latvian.kubejs.mixin.common;

import dev.latvian.kubejs.core.FireworkRocketEntityKJS;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author LatvianModder
 */
@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketEntityMixin implements FireworkRocketEntityKJS {
	@Override
	@Accessor("lifetime")
	public abstract void setLifetimeKJS(int lifetime);
}