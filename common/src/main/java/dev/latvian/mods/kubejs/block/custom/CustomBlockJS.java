package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.generator.DataJsonGenerator;

public interface CustomBlockJS {
	void generateAssets(BlockBuilder builder, AssetJsonGenerator generator);

	default void generateData(BlockBuilder builder, DataJsonGenerator generator) {
	}
}
