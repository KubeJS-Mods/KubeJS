package dev.latvian.kubejs.mixin.common;

import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.core.ItemKJS;
import dev.latvian.kubejs.item.ItemBuilder;
import dev.latvian.mods.rhino.util.RemapForJS;
import me.shedaniel.architectury.registry.fuel.FuelRegistry;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
	public abstract void setMaxStackSizeKJS(int i);

	@Override
	@Accessor("maxDamage")
	public abstract void setMaxDamageKJS(int i);

	@Override
	@Accessor("craftingRemainingItem")
	public abstract void setCraftingRemainderKJS(Item i);

	@Override
	@Accessor("isFireResistant")
	public abstract void setFireResistantKJS(boolean b);

	@Override
	@Accessor("rarity")
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
	public abstract void setFoodPropertiesKJS(FoodProperties properties);

	@Inject(method = "isFoil", at = @At("HEAD"), cancellable = true)
	private void isFoilKJS(ItemStack itemStack, CallbackInfoReturnable<Boolean> ci) {
		if (itemBuilderKJS != null && itemBuilderKJS.glow) {
			ci.setReturnValue(true);
		}
	}
}
