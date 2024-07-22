package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.core.ItemStackKJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import java.util.stream.Stream;

@Mixin(ItemStack.class)
@RemapPrefixForJS("kjs$")
public abstract class ItemStackMixin implements ItemStackKJS {
	@Shadow
	@HideFromJS
	public abstract void enchant(Holder<Enchantment> enchantment, int level);

	@Shadow
	@HideFromJS
	public abstract ItemEnchantments getEnchantments();

	@Shadow
	@HideFromJS
	public abstract Stream<TagKey<Item>> getTags();

	@ModifyConstant(method = "lambda$static$3", constant = @Constant(intValue = 99))
	private static int kjs$maxSlotSize(int original) {
		return CommonProperties.get().getMaxSlotSize(original);
	}
}
