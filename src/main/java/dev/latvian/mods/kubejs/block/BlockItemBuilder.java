package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public class BlockItemBuilder extends ItemBuilder {
	public BlockBuilder blockBuilder;

	public BlockItemBuilder(Identifier i) {
		super(i);
	}

	@Override
	public Item createObject() {
		return new BlockItem(blockBuilder.get(), createItemProperties());
	}

	@Override
	public String getTranslationKeyGroup() {
		return "block";
	}

	@Override
	public void generateAssets(KubeAssetGenerator generator) {
	}
}
