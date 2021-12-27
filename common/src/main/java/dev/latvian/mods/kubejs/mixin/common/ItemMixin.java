package dev.latvian.mods.kubejs.mixin.common;

import dev.architectury.registry.fuel.FuelRegistry;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.core.ItemKJS;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * @author LatvianModder
 */
@Mixin(value = Item.class, priority = 1001)
public abstract class ItemMixin implements ItemKJS {
	@Unique
	private ItemBuilder itemBuilderKJS;

	@Override
	@Nullable
	public ItemBuilder getItemBuilderKJS() {
		return itemBuilderKJS;
	}

	@Override
	public void setItemBuilderKJS(ItemBuilder b) {
		itemBuilderKJS = b;
	}

	@Override
	@Accessor("maxStackSize")
	@Mutable
	public abstract void setMaxStackSizeKJS(int i);

	@Override
	@Accessor("maxDamage")
	@Mutable
	public abstract void setMaxDamageKJS(int i);

	@Override
	@Accessor("craftingRemainingItem")
	@Mutable
	public abstract void setCraftingRemainderKJS(Item i);

	@Override
	@Accessor("isFireResistant")
	@Mutable
	public abstract void setFireResistantKJS(boolean b);

	@Override
	@Accessor("rarity")
	@Mutable
	public abstract void setRarityKJS(Rarity r);

	@Override
	@RemapForJS("setBurnTime")
	public void setBurnTimeKJS(int i) {
		FuelRegistry.register(i, (Item) (Object) this);
	}

	@RemapForJS("getId")
	public String getIdKJS() {
		return KubeJSRegistries.items().getId((Item) (Object) this).toString();
	}

	@Override
	@Accessor("foodProperties")
	@Mutable
	public abstract void setFoodPropertiesKJS(FoodProperties properties);

	@Inject(method = "isFoil", at = @At("HEAD"), cancellable = true)
	private void isFoilKJS(ItemStack itemStack, CallbackInfoReturnable<Boolean> ci) {
		if (itemBuilderKJS != null && itemBuilderKJS.glow) {
			ci.setReturnValue(true);
		}
	}

	@Inject(method = "appendHoverText", at = @At("RETURN"))
	private void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn, CallbackInfo ci) {
		if (itemBuilderKJS != null && !itemBuilderKJS.tooltip.isEmpty()) {
			tooltip.addAll(itemBuilderKJS.tooltip);
		}
	}
}
