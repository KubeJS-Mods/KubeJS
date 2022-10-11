package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.core.ItemStackKJS;
import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
@RemapPrefixForJS("kjs$")
public abstract class ItemStackMixin implements ItemStackKJS {
	private double kjs$chance = Double.NaN;

	@Shadow
	@RemapForJS("enchantStack")
	public abstract void enchant(Enchantment enchantment, int level);

	@Shadow
	@RemapForJS("getNbt")
	public abstract CompoundTag getTag();

	@Shadow
	@RemapForJS("setNbt")
	public abstract void setTag(CompoundTag tag);

	@Shadow
	@RemapForJS("hasNBT")
	public abstract boolean hasTag();

	@Inject(method = "copy", at = @At("RETURN"), cancellable = true)
	private void kjs$copy(CallbackInfoReturnable<ItemStack> cir) {
		if (!Double.isNaN(kjs$chance)) {
			var is = cir.getReturnValue();
			is.kjs$setChance(kjs$chance);
			cir.setReturnValue(is);
		}
	}

	@Override
	public double kjs$getChance() {
		return kjs$chance;
	}

	@Override
	public void kjs$setChance(double chance) {
		if (!kjs$self().isEmpty()) {
			kjs$chance = chance;
		}
	}

	@Override
	public ItemStack kjs$withChance(double chance) {
		if (kjs$self().isEmpty()) {
			return ItemStack.EMPTY;
		}

		var is = kjs$self().copy();
		is.kjs$setChance(chance);
		return is;
	}
}
