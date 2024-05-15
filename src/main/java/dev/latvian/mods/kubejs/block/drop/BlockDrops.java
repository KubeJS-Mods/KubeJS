package dev.latvian.mods.kubejs.block.drop;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public record BlockDrops(BlockDrop[] items, NumberProvider rolls) {
	public static final BlockDrops EMPTY = new BlockDrops(new BlockDrop[0], null);

	public static BlockDrops createDefault(Item item) {
		return new BlockDrops(new BlockDrop[]{new BlockDrop(item, 1, null)}, ConstantValue.exactly(1F));
	}
}
