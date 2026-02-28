package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.core.ItemStackKJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.stream.Stream;

@Mixin(ItemStack.class)
@RemapPrefixForJS("kjs$")
public abstract class ItemStackMixin implements ItemStackKJS {

	@Shadow
	@Final
	PatchedDataComponentMap components;

	@Shadow
	@HideFromJS
	public abstract void enchant(Holder<Enchantment> enchantment, int level);

	@Shadow
	@HideFromJS
	public abstract ItemEnchantments getEnchantments();

	@Shadow
	@HideFromJS
	public abstract Holder<Item> typeHolder();

	// Moved to ExtraCodecsMixin which should now intercept globally
	/*@ModifyConstant(method = "lambda$static$3", constant = @Constant(intValue = 99))
	private static int kjs$maxSlotSize(int original) {
		return CommonProperties.get().getMaxSlotSize(original);
	}*/

	@Override
	@ReturnsSelf
	public ItemStackKJS kjs$resetComponents(Context cx) {
		components.restorePatch(DataComponentPatch.EMPTY);
		return this;
	}

	@HideFromJS
	public static <T> void kjs$overrideComponent(ItemStack stack, DataComponentType<T> type, @Nullable T value) {
		stack.set(type, value);
	}

	@Inject(
		method = "finishUsingItem(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;",
		at = @At("HEAD")
	)
	private void kjs$onFoodFinished(Level level, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir) {
		if (!(entity instanceof Player player)) {
			return;
		}

		var consumable = kjs$self().get(DataComponents.CONSUMABLE);
		if (consumable == null || !kjs$self().has(DataComponents.FOOD)) {
			return;
		}

		entity.kjs$foodEaten((ItemStack) (Object) this);
	}
}
