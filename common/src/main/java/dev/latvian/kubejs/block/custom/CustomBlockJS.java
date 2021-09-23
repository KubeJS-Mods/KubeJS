package dev.latvian.kubejs.block.custom;

import dev.latvian.kubejs.block.BlockBuilder;
import dev.latvian.kubejs.generator.AssetJsonGenerator;
import dev.latvian.kubejs.generator.DataJsonGenerator;

public interface CustomBlockJS {
	void generateAssets(BlockBuilder builder, AssetJsonGenerator generator);

	default void generateData(BlockBuilder builder, DataJsonGenerator generator) {
	}
}
