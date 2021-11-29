package dev.latvian.mods.kubejs.mixin.common;

import dev.latvian.mods.kubejs.core.ItemKJS;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * @author LatvianModder
 */
@Mixin(Item.class)
public abstract class ItemClientMixin {
	@Inject(method = "appendHoverText", at = @At("RETURN"))
	private void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn, CallbackInfo ci) {
		if (this instanceof ItemKJS && ((ItemKJS) this).getItemBuilderKJS() != null && !((ItemKJS) this).getItemBuilderKJS().tooltip.isEmpty()) {
			tooltip.addAll(((ItemKJS) this).getItemBuilderKJS().tooltip);
		}
	}
}
