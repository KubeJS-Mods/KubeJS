package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.ItemKJS;
import dev.latvian.mods.kubejs.item.ItemBehavior;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.extensions.IItemExtension;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = IItemExtension.class, priority = 999)
public interface IItemExtensionMixin {
	@Unique
	@Nullable
	private ItemBehavior kjs$getItemBehavior() {
		return this instanceof ItemKJS kjs ? kjs.kjs$getItemBehavior() : null;
	}

	@Inject(method = "isPiglinCurrency", at = @At("HEAD"), cancellable = true)
	private void isPiglinCurrency(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		ItemBehavior behavior = kjs$getItemBehavior();
		if (behavior != null && behavior.isPiglinCurrency != null) {
			cir.setReturnValue(behavior.isPiglinCurrency.test(stack));
		}
	}

	@Inject(method = "isGazeDisguise", at = @At("HEAD"), cancellable = true)
	private void isGazeDisguise(ItemStack stack, Player player, @Nullable LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
		ItemBehavior behavior = kjs$getItemBehavior();
		if (behavior != null && behavior.protectFromGaze != null) {
			cir.setReturnValue(behavior.protectFromGaze.test(stack, player, entity));
		}
	}

	@Inject(method = "makesPiglinsNeutral", at = @At("HEAD"), cancellable = true)
	private void makesPiglinsNeutral(ItemStack stack, LivingEntity wearer, CallbackInfoReturnable<Boolean> cir) {
		ItemBehavior behavior = kjs$getItemBehavior();
		if (behavior != null && behavior.makesPiglinsNeutral != null) {
			cir.setReturnValue(behavior.makesPiglinsNeutral.test(stack, wearer));
		}
	}

	@Inject(method = "getCraftingRemainder", at = @At("HEAD"), cancellable = true)
	private void getCraftingRemainder(ItemInstance instance, CallbackInfoReturnable<ItemStackTemplate> cir) {
		ItemBehavior behavior = kjs$getItemBehavior();
		// TODO: rework with the rest of stack => instance changes
		if (behavior != null && behavior.craftingRemainingItem != null && instance instanceof ItemStack stack) {
			var result = behavior.craftingRemainingItem.apply(stack);
			cir.setReturnValue(result.isEmpty() ? null : ItemStackTemplate.fromNonEmptyStack(result));
		}
	}

	@Inject(method = "getEntityLifespan", at = @At("HEAD"), cancellable = true)
	private void getEntityLifespan(ItemStack itemStack, Level level, CallbackInfoReturnable<Integer> cir) {
		ItemBehavior behavior = kjs$getItemBehavior();
		if (behavior != null && behavior.getEntityLifespan != null) {
			cir.setReturnValue(behavior.getEntityLifespan.applyAsInt(itemStack, level));
		}
	}

	@Inject(method = "canWalkOnPowderedSnow", at = @At("HEAD"), cancellable = true)
	private void canWalkOnPowderedSnow(ItemStack stack, LivingEntity wearer, CallbackInfoReturnable<Boolean> cir) {
		ItemBehavior behavior = kjs$getItemBehavior();
		if (behavior != null && behavior.canWalkOnPowderedSnow != null) {
			cir.setReturnValue(behavior.canWalkOnPowderedSnow.test(stack, wearer));
		}
	}

	@Inject(method = "canPerformAction", at = @At("HEAD"), cancellable = true)
	private void canPerformAction(ItemInstance stack, ItemAbility itemAbility, CallbackInfoReturnable<Boolean> cir) {
		ItemBehavior behavior = kjs$getItemBehavior();
		if (behavior != null && behavior.canPerformAction != null) {
			cir.setReturnValue(behavior.canPerformAction.test(stack, itemAbility));
		}
	}

	@Inject(method = "canBeHurtBy", at = @At("HEAD"), cancellable = true)
	private void canBeHurtBy(ItemStack stack, DamageSource source, CallbackInfoReturnable<Boolean> cir) {
		ItemBehavior behavior = kjs$getItemBehavior();
		if (behavior != null && behavior.canBeHurtBy != null) {
			cir.setReturnValue(behavior.canBeHurtBy.test(stack, source));
		}
	}

	@Inject(method = "applyEnchantments", at = @At("HEAD"), cancellable = true)
	private void applyEnchantments(ItemStack stack, List<EnchantmentInstance> enchantments, CallbackInfoReturnable<ItemStack> cir) {
		ItemBehavior behavior = kjs$getItemBehavior();
		if (behavior != null && behavior.applyEnchantments != null) {
			cir.setReturnValue(behavior.applyEnchantments.apply(stack, enchantments));
		}
	}
}
