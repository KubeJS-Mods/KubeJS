package dev.latvian.mods.kubejs.block.drop;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public record BlockDrops(ItemStack[] items, NumberProvider rolls) {
	public static final BlockDrops EMPTY = new BlockDrops(new ItemStack[0], null);

	public static BlockDrops createDefault(ItemStack item) {
		return new BlockDrops(new ItemStack[]{item}, ConstantValue.exactly(1F));
	}
}
