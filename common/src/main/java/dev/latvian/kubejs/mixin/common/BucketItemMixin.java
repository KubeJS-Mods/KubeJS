package dev.latvian.kubejs.mixin.common;

import dev.latvian.kubejs.core.BucketItemKJS;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author LatvianModder
 */
@Mixin(BucketItem.class)
public abstract class BucketItemMixin implements BucketItemKJS {
	@Accessor("content")
	public abstract Fluid getFluidKJS();
}
