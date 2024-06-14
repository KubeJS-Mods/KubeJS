package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.ItemStackKJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemStack.class)
@RemapPrefixForJS("kjs$")
public abstract class ItemStackMixin implements ItemStackKJS {
	@Shadow
	@HideFromJS
	public abstract void enchant(Holder<Enchantment> enchantment, int level);

	@Shadow
	@HideFromJS
	public abstract ItemEnchantments getEnchantments();
}
