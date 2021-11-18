package dev.latvian.kubejs.enchantment;

import dev.latvian.kubejs.item.ItemStackJS;
import net.minecraft.world.item.ItemStack;

public class CustomEnchantCallbackJS {
	public final ItemStackJS item;
	public final EnchantmentJS enchantment;

	public CustomEnchantCallbackJS(ItemStack item, EnchantmentJS enchantment) {
        this.item = ItemStackJS.of(item);
		this.enchantment = enchantment;
    }

	public ItemStackJS getItem() {
        return item;
    }

    public EnchantmentJS getEnchantment() {
        return enchantment;
    }

}
