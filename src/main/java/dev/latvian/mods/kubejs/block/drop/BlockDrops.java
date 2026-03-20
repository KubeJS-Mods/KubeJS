package dev.latvian.mods.kubejs.block.drop;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.jspecify.annotations.Nullable;

public record BlockDrops(ItemStack[] items, @Nullable NumberProvider rolls, @Nullable Item defaultItem) {
	public static final BlockDrops EMPTY = new BlockDrops(new ItemStack[0], null, null);

	public static BlockDrops createDefault(Item item) {
		return new BlockDrops(new ItemStack[0], ConstantValue.exactly(1F), item);
	}

	public static BlockDrops createStack(ItemStack item) {
		return new BlockDrops(new ItemStack[]{item}, ConstantValue.exactly(1F), null);
	}
}
