package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.ItemKJS;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Mixin(value = Item.class, priority = 1001)
@RemapPrefixForJS("kjs$")
public abstract class ItemMixin implements ItemKJS {
	@Shadow
	public abstract DataComponentMap components();

	@Shadow
	@Final
	private Holder.Reference<Item> builtInRegistryHolder;

	@Unique
	private @Nullable ItemBuilder kjs$itemBuilder;

	@Unique
	private @Nullable Map<String, Object> kjs$typeData;

	@Unique
	private @Nullable ResourceKey<Item> kjs$registryKey;

	@Unique
	private @Nullable String kjs$id;

	@Override
	@Nullable
	public ItemBuilder kjs$getItemBuilder() {
		return kjs$itemBuilder;
	}

	@Override
	public Holder.Reference<Item> kjs$asHolder() {
		return builtInRegistryHolder;
	}

	@Override
	public ResourceKey<Item> kjs$getKey() {
		return kjs$asHolder().getKey();
	}

	@Override
	public String kjs$getId() {
		if (kjs$id == null) {
			kjs$id = ItemKJS.super.kjs$getId();
		}

		return kjs$id;
	}

	@Override
	public void kjs$setItemBuilder(ItemBuilder b) {
		kjs$itemBuilder = b;
	}

	@Override
	public Map<String, Object> kjs$getTypeData() {
		if (kjs$typeData == null) {
			kjs$typeData = new HashMap<>();
		}

		return kjs$typeData;
	}

	@Override
	@Accessor("craftingRemainingItem")
	@Mutable
	public abstract void kjs$setCraftingRemainder(ItemStackTemplate i);

	@Inject(method = "isFoil", at = @At("HEAD"), cancellable = true)
	private void isFoil(ItemStack itemStack, CallbackInfoReturnable<Boolean> ci) {
		if (kjs$itemBuilder != null && kjs$itemBuilder.glow) {
			ci.setReturnValue(true);
		}
	}

	@Inject(method = "appendHoverText", at = @At("RETURN"))
	private void appendHoverText(
		ItemStack itemStack,
		Item.TooltipContext context,
		TooltipDisplay display,
		Consumer<Component> builder,
		TooltipFlag tooltipFlag,
		CallbackInfo ci
	) {
		if (kjs$itemBuilder != null && !kjs$itemBuilder.tooltip.isEmpty()) {
			for (var c : kjs$itemBuilder.tooltip) {
				builder.accept(c);
			}
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
			ci.setReturnValue(kjs$itemBuilder.barColor.apply(stack).kjs$getRGB());
		}
	}

	@Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
	private void getUseDuration(ItemStack itemStack, LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
		if (kjs$itemBuilder != null && kjs$itemBuilder.useDuration != null) {
			cir.setReturnValue(kjs$itemBuilder.useDuration.applyAsInt(itemStack, entity));
		}
	}

	@Inject(method = "getUseAnimation", at = @At("HEAD"), cancellable = true)
	private void getUseAnimation(ItemStack itemStack, CallbackInfoReturnable<ItemUseAnimation> ci) {
		if (kjs$itemBuilder != null && kjs$itemBuilder.anim != null) {
			ci.setReturnValue(kjs$itemBuilder.anim);
		}
	}

	@Inject(method = "getName", at = @At("HEAD"), cancellable = true)
	private void getName(ItemStack itemStack, CallbackInfoReturnable<Component> ci) {
		if (kjs$itemBuilder != null && kjs$itemBuilder.nameGetter != null) {
			ci.setReturnValue(kjs$itemBuilder.nameGetter.apply(itemStack));
		}

		if (kjs$itemBuilder != null && kjs$itemBuilder.displayName != null && kjs$itemBuilder.formattedDisplayName) {
			ci.setReturnValue(kjs$itemBuilder.displayName);
		}
	}

	@Inject(method = "use", at = @At("HEAD"), cancellable = true)
	private void use(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		if (kjs$itemBuilder != null && kjs$itemBuilder.use != null) {
			if (kjs$itemBuilder.use.use(level, player, hand)) {
				cir.setReturnValue(ItemUtils.startUsingInstantly(level, player, hand));
			} else {
				cir.setReturnValue(InteractionResult.FAIL);
			}
		}
	}

	@Inject(method = "finishUsingItem", at = @At("HEAD"), cancellable = true)
	private void finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> ci) {
		if (kjs$itemBuilder != null && kjs$itemBuilder.finishUsing != null) {
			ci.setReturnValue(kjs$itemBuilder.finishUsing.finishUsingItem(itemStack, level, livingEntity));
		}
	}

	@Inject(method = "releaseUsing", at = @At("HEAD"), cancellable = true)
	private void releaseUsing(ItemStack itemStack, Level level, LivingEntity entity, int remainingTime, CallbackInfoReturnable<Boolean> cir) {
		if (kjs$itemBuilder != null && kjs$itemBuilder.releaseUsing != null) {
			kjs$itemBuilder.releaseUsing.releaseUsing(itemStack, level, entity, remainingTime);
		}
	}

	@Inject(method = "hurtEnemy", at = @At("HEAD"), cancellable = true)
	private void hurtEnemy(ItemStack itemStack, LivingEntity mob, LivingEntity attacker, CallbackInfo ci) {
		if (kjs$itemBuilder != null && kjs$itemBuilder.hurtEnemy != null) {
			kjs$itemBuilder.hurtEnemy.test(new ItemBuilder.HurtEnemyContext(itemStack, mob, attacker));
		}
	}

	@Override
	public Ingredient kjs$asIngredient() {
		return Ingredient.of(kjs$self());
	}

	@Override
	@Accessor("descriptionId")
	@Mutable
	public abstract void kjs$setNameKey(String key);

	@Override
	@Accessor("canCombineRepair")
	@Mutable
	public abstract void kjs$setCanRepair(boolean repairable);
}
