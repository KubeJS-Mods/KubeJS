package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.item.ItemTintFunction;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;

public record ItemTintFunctionWrapper(ItemTintFunction function) implements ItemColor {
	@Override
	public int getColor(ItemStack stack, int index) {
		var c = function.getColor(stack, index);
		return c == null ? 0xFFFFFFFF : c.kjs$getARGB();
	}
}
