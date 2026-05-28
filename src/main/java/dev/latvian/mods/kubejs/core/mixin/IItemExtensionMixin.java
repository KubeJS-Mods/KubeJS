package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.core.ItemKJS;
import dev.latvian.mods.kubejs.item.ItemBehavior;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.extensions.IItemExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = IItemExtension.class, priority = 999)
public interface IItemExtensionMixin {
	@Unique
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

	@Inject(method = "isEnderMask", at = @At("HEAD"), cancellable = true)
	private void isEnderMask(ItemStack stack, Player player, EnderMan endermanEntity, CallbackInfoReturnable<Boolean> cir) {
		ItemBehavior behavior = kjs$getItemBehavior();
		if (behavior != null && behavior.isEnderMask != null) {
			cir.setReturnValue(behavior.isEnderMask.test(stack, player, endermanEntity));
		}
	}

	@Inject(method = "makesPiglinsNeutral", at = @At("HEAD"), cancellable = true)
	private void makesPiglinsNeutral(ItemStack stack, LivingEntity wearer, CallbackInfoReturnable<Boolean> cir) {
		ItemBehavior behavior = kjs$getItemBehavior();
		if (behavior != null && behavior.makesPiglinsNeutral != null) {
			cir.setReturnValue(behavior.makesPiglinsNeutral.test(stack, wearer));
		}
	}

	@Inject(method = "getCraftingRemainingItem", at = @At("HEAD"), cancellable = true)
	private void getCraftingRemainingItem(ItemStack itemStack, CallbackInfoReturnable<ItemStack> cir) {
		ItemBehavior behavior = kjs$getItemBehavior();
		if (behavior != null && behavior.craftingRemainingItem != null) {
			cir.setReturnValue(behavior.craftingRemainingItem.apply(itemStack));
		}
	}

	@Inject(method = "hasCraftingRemainingItem", at = @At("HEAD"), cancellable = true)
	private void hasCraftingRemainingItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		ItemBehavior behavior = kjs$getItemBehavior();
		if (behavior != null && behavior.craftingRemainingItem != null) {
			var returnStack = behavior.craftingRemainingItem.apply(stack);
			cir.setReturnValue(returnStack != null && !returnStack.isEmpty());
		}
	}

	@Inject(method = "getEntityLifespan", at = @At("HEAD"), cancellable = true)
	private void getEntityLifespan(ItemStack itemStack, Level level, CallbackInfoReturnable<Integer> cir) {
		ItemBehavior behavior = kjs$getItemBehavior();
		if (behavior != null && behavior.getEntityLifespan != null) {
			cir.setReturnValue(behavior.getEntityLifespan.applyAsInt(itemStack, level));
		}
	}

	@Inject(method = "canDisableShield", at = @At("HEAD"), cancellable = true)
	private void canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker, CallbackInfoReturnable<Boolean> cir) {
		ItemBehavior behavior = kjs$getItemBehavior();
		if (behavior != null && behavior.canDisableShield != null) {
			cir.setReturnValue(behavior.canDisableShield.test(stack, shield, attacker));
		}
	}

	@Inject(method = "canElytraFly", at = @At("HEAD"), cancellable = true)
	private void canElytraFly(ItemStack stack, LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
		ItemBehavior behavior = kjs$getItemBehavior();
		if (behavior != null && behavior.canElytraFly != null) {
			cir.setReturnValue(behavior.canElytraFly.test(stack, entity));
		}
	}

	@Inject(method = "elytraFlightTick", at = @At("HEAD"), cancellable = true)
	private void elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks, CallbackInfoReturnable<Boolean> cir) {
		ItemBehavior behavior = kjs$getItemBehavior();
		if (behavior != null && behavior.elytraFlightTick != null) {
			cir.setReturnValue(behavior.elytraFlightTick.flightTick(stack, entity, flightTicks));
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
	private void canPerformAction(ItemStack stack, ItemAbility itemAbility, CallbackInfoReturnable<Boolean> cir) {
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
