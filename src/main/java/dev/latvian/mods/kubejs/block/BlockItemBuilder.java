package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.client.LangKubeEvent;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public class BlockItemBuilder extends ItemBuilder {
	public final BlockBuilder blockBuilder;

	public BlockItemBuilder(BlockBuilder b, Identifier i) {
		super(i);
		this.blockBuilder = b;
	}

	@Override
	public Item createObject() {
		return new BlockItem(blockBuilder.get(),
			createItemProperties()
				.overrideDescription(blockBuilder.getBuilderTranslationKey())
		);
	}

	@Override
	public String getTranslationKeyGroup() {
		return "block";
	}

	@Override
	public void generateAssets(KubeAssetGenerator generator) {
	}

	@Override
	public void generateLang(LangKubeEvent lang) {
		// To allow subclasses like SeedItemBuilder to still generate normally
		if (getClass() != BlockItemBuilder.class) {
			super.generateLang(lang);
		}
	}
}
