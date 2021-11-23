package dev.latvian.kubejs.enchantment;

import dev.latvian.kubejs.item.ItemStackJS;
import net.minecraft.world.item.ItemStack;

public class CustomEnchantCallbackJS {
	public final ItemStackJS item;
	public final EnchantmentJS enchantment;
	public boolean canEnchant = false;

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

	public void setCanEnchant(boolean canEnchant) {
        this.canEnchant = canEnchant;
    }

	public boolean canEnchant() {
        return this.canEnchant;
    }

	public void allow() {
        this.canEnchant = true;
    }

	@Override
	public String toString() {
		return "CustomEnchantCallbackJS{" +
				"item=" + item +
				", enchantment=" + enchantment +
				", canEnchant=" + canEnchant +
				'}';
	}
}
