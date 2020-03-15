package dev.latvian.kubejs.mixin;

import dev.latvian.kubejs.KubeJSCore;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

/**
 * @author LatvianModder
 */
@Mixin(NetworkTagManager.ReloadResults.class)
public abstract class NetworkTagManagerReloadResultsMixin<T>
{
	@Inject(method = "<init>", at = @At("RETURN"))
	private void customTags(Map<ResourceLocation, Tag.Builder<Block>> blocks, Map<ResourceLocation, Tag.Builder<Item>> items, Map<ResourceLocation, Tag.Builder<Fluid>> fluids, Map<ResourceLocation, Tag.Builder<EntityType<?>>> entityTypes, CallbackInfo ci)
	{
		KubeJSCore.customTags(blocks, items, fluids, entityTypes);
	}
}