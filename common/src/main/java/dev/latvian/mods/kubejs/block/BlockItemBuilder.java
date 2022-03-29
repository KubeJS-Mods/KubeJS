package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

/**
 * @author LatvianModder
 */
public class BlockItemBuilder extends ItemBuilder {
	public BlockBuilder blockBuilder;

	public BlockItemBuilder(ResourceLocation i) {
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
	public void generateAssetJsons(AssetJsonGenerator generator) {
	}
}
