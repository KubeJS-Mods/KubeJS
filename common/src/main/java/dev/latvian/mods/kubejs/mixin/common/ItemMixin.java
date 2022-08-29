package dev.latvian.mods.kubejs.mixin.common;

import dev.architectury.registry.fuel.FuelRegistry;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.core.ItemKJS;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.level.LevelJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
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
	@Shadow
	@Final
	public static int MAX_BAR_WIDTH;

	@Unique
	private ItemBuilder itemBuilderKJS;

	@Unique
	private final CompoundTag typeDataKJS = new CompoundTag();

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
	@RemapForJS("getTypeData")
	public CompoundTag getTypeDataKJS() {
		return typeDataKJS;
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

	@Inject(method = "isBarVisible", at = @At("HEAD"), cancellable = true)
	private void isBarVisible(ItemStack stack, CallbackInfoReturnable<Boolean> ci) {
		if (itemBuilderKJS != null && itemBuilderKJS.barWidth != null && itemBuilderKJS.barWidth.applyAsInt(ItemStackJS.of(stack)) <= MAX_BAR_WIDTH) {
			ci.setReturnValue(true);
		}
	}

	@Inject(method = "getBarWidth", at = @At("HEAD"), cancellable = true)
	private void getBarWidth(ItemStack stack, CallbackInfoReturnable<Integer> ci) {
		if (itemBuilderKJS != null && itemBuilderKJS.barWidth != null) {
			ci.setReturnValue(itemBuilderKJS.barWidth.applyAsInt(ItemStackJS.of(stack)));
		}
	}

	@Inject(method = "getBarColor", at = @At("HEAD"), cancellable = true)
	private void getBarColor(ItemStack stack, CallbackInfoReturnable<Integer> ci) {
		if (itemBuilderKJS != null && itemBuilderKJS.barColor != null) {
			ci.setReturnValue(itemBuilderKJS.barColor.apply(ItemStackJS.of(stack)).getRgbJS());
		}
	}

	@Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
	private void getUseDuration(ItemStack itemStack, CallbackInfoReturnable<Integer> ci) {
		if (itemBuilderKJS != null && itemBuilderKJS.useDuration != null) {
			ci.setReturnValue(itemBuilderKJS.useDuration.applyAsInt(ItemStackJS.of(itemStack)));
		}
	}

	@Inject(method = "getUseAnimation", at = @At("HEAD"), cancellable = true)
	private void getUseAnimation(ItemStack itemStack, CallbackInfoReturnable<UseAnim> ci) {
		if (itemBuilderKJS != null && itemBuilderKJS.anim != null) {
			ci.setReturnValue(itemBuilderKJS.anim);
		}
	}

	@Inject(method = "getName", at = @At("HEAD"), cancellable = true)
	private void getName(ItemStack itemStack, CallbackInfoReturnable<Component> ci) {
		if (itemBuilderKJS != null && itemBuilderKJS.nameGetter != null) {
			ci.setReturnValue(itemBuilderKJS.nameGetter.apply(ItemStackJS.of(itemStack)));
		}
	}

	@Inject(method = "use", at = @At("HEAD"), cancellable = true)
	private void use(Level level, Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> ci) {
		if (itemBuilderKJS != null && itemBuilderKJS.use != null) {
			ItemStack itemStack = player.getItemInHand(interactionHand);
			LevelJS levelJS = UtilsJS.getLevel(level);
			if (itemBuilderKJS.use.use(levelJS, levelJS.getPlayer(player), interactionHand)) {
				ci.setReturnValue(ItemUtils.startUsingInstantly(level, player, interactionHand));
			} else {
				ci.setReturnValue(InteractionResultHolder.fail(itemStack));
			}
		}
	}

	@Inject(method = "finishUsingItem", at = @At("HEAD"), cancellable = true)
	private void finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> ci) {
		if (itemBuilderKJS != null && itemBuilderKJS.finishUsing != null) {
			LevelJS levelJS = UtilsJS.getLevel(level);
			ci.setReturnValue(itemBuilderKJS.finishUsing.finishUsingItem(ItemStackJS.of(itemStack), levelJS, levelJS.getLivingEntity(livingEntity)).getItemStack());
		}
	}

	@Inject(method = "releaseUsing", at = @At("HEAD"))
	private void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int i, CallbackInfo ci) {
		if (itemBuilderKJS != null && itemBuilderKJS.releaseUsing != null) {
			LevelJS levelJS = UtilsJS.getLevel(level);
			itemBuilderKJS.releaseUsing.releaseUsing(ItemStackJS.of(itemStack), levelJS, levelJS.getLivingEntity(livingEntity), i);
		}
	}
}
