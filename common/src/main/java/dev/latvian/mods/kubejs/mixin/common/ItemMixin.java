package dev.latvian.mods.kubejs.mixin.common;

import dev.architectury.registry.fuel.FuelRegistry;
import dev.latvian.mods.kubejs.core.ItemKJS;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.level.LevelJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
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
@RemapPrefixForJS("kjs$")
public abstract class ItemMixin implements ItemKJS {
	private ItemBuilder kjs$itemBuilder;
	private CompoundTag kjs$typeData;

	@Override
	@Nullable
	public ItemBuilder kjs$getItemBuilder() {
		return kjs$itemBuilder;
	}

	@Override
	@RemapForJS("getItem")
	public Item kjs$self() {
		return (Item) (Object) this;
	}

	@Override
	public void kjs$setItemBuilder(ItemBuilder b) {
		kjs$itemBuilder = b;
	}

	@Override
	public CompoundTag kjs$getTypeData() {
		if (kjs$typeData == null) {
			kjs$typeData = new CompoundTag();
		}

		return kjs$typeData;
	}

	@Override
	@Accessor("maxStackSize")
	@Mutable
	public abstract void kjs$setMaxStackSize(int i);

	@Override
	@Accessor("maxDamage")
	@Mutable
	public abstract void kjs$setMaxDamage(int i);

	@Override
	@Accessor("craftingRemainingItem")
	@Mutable
	public abstract void kjs$setCraftingRemainder(Item i);

	@Override
	@Accessor("isFireResistant")
	@Mutable
	public abstract void kjs$setFireResistant(boolean b);

	@Override
	@Accessor("rarity")
	@Mutable
	public abstract void kjs$setRarity(Rarity r);

	@Override
	@RemapForJS("setBurnTime")
	public void kjs$setBurnTime(int i) {
		FuelRegistry.register(i, (Item) (Object) this);
	}

	@Override
	@Accessor("foodProperties")
	@Mutable
	public abstract void kjs$setFoodProperties(FoodProperties properties);

	@Inject(method = "isFoil", at = @At("HEAD"), cancellable = true)
	private void isFoilKJS(ItemStack itemStack, CallbackInfoReturnable<Boolean> ci) {
		if (kjs$itemBuilder != null && kjs$itemBuilder.glow) {
			ci.setReturnValue(true);
		}
	}

	@Inject(method = "appendHoverText", at = @At("RETURN"))
	private void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn, CallbackInfo ci) {
		if (kjs$itemBuilder != null && !kjs$itemBuilder.tooltip.isEmpty()) {
			tooltip.addAll(kjs$itemBuilder.tooltip);
		}
	}

	@Inject(method = "isBarVisible", at = @At("HEAD"), cancellable = true)
	private void isBarVisible(ItemStack stack, CallbackInfoReturnable<Boolean> ci) {
		if (kjs$itemBuilder != null && kjs$itemBuilder.barWidth != null && kjs$itemBuilder.barWidth.applyAsInt(stack) <= Item.MAX_BAR_WIDTH) {
			ci.setReturnValue(true);
		}
	}

	@Inject(method = "getBarWidth", at = @At("HEAD"), cancellable = true)
	private void getBarWidth(ItemStack stack, CallbackInfoReturnable<Integer> ci) {
		if (kjs$itemBuilder != null && kjs$itemBuilder.barWidth != null) {
			ci.setReturnValue(kjs$itemBuilder.barWidth.applyAsInt(stack));
		}
	}

	@Inject(method = "getBarColor", at = @At("HEAD"), cancellable = true)
	private void getBarColor(ItemStack stack, CallbackInfoReturnable<Integer> ci) {
		if (kjs$itemBuilder != null && kjs$itemBuilder.barColor != null) {
			ci.setReturnValue(kjs$itemBuilder.barColor.apply(stack).getRgbJS());
		}
	}

	@Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
	private void getUseDuration(ItemStack itemStack, CallbackInfoReturnable<Integer> ci) {
		if (kjs$itemBuilder != null && kjs$itemBuilder.useDuration != null) {
			ci.setReturnValue(kjs$itemBuilder.useDuration.applyAsInt(itemStack));
		}
	}

	@Inject(method = "getUseAnimation", at = @At("HEAD"), cancellable = true)
	private void getUseAnimation(ItemStack itemStack, CallbackInfoReturnable<UseAnim> ci) {
		if (kjs$itemBuilder != null && kjs$itemBuilder.anim != null) {
			ci.setReturnValue(kjs$itemBuilder.anim);
		}
	}

	@Inject(method = "use", at = @At("HEAD"), cancellable = true)
	private void use(Level level, Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> ci) {
		if (kjs$itemBuilder != null && kjs$itemBuilder.use != null) {
			ItemStack itemStack = player.getItemInHand(interactionHand);
			LevelJS levelJS = UtilsJS.getLevel(level);
			if (kjs$itemBuilder.use.use(levelJS, levelJS.getPlayer(player), interactionHand)) {
				ci.setReturnValue(ItemUtils.startUsingInstantly(level, player, interactionHand));
			} else {
				ci.setReturnValue(InteractionResultHolder.fail(itemStack));
			}
		}
	}

	@Inject(method = "finishUsingItem", at = @At("HEAD"), cancellable = true)
	private void finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> ci) {
		if (kjs$itemBuilder != null && kjs$itemBuilder.finishUsing != null) {
			LevelJS levelJS = UtilsJS.getLevel(level);
			ci.setReturnValue(kjs$itemBuilder.finishUsing.finishUsingItem(ItemStackJS.of(itemStack), levelJS, levelJS.getLivingEntity(livingEntity)).getItemStack());
		}
	}

	@Inject(method = "releaseUsing", at = @At("HEAD"))
	private void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int i, CallbackInfo ci) {
		if (kjs$itemBuilder != null && kjs$itemBuilder.releaseUsing != null) {
			LevelJS levelJS = UtilsJS.getLevel(level);
			kjs$itemBuilder.releaseUsing.releaseUsing(ItemStackJS.of(itemStack), levelJS, levelJS.getLivingEntity(livingEntity), i);
		}
	}
}
