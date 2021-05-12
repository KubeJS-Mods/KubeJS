package dev.latvian.kubejs.block;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class DetectorInstance {
	public final String id;
	public Supplier<Block> block;
	public Supplier<Item> item;

	public DetectorInstance(String i) {
		id = i;
	}
}
