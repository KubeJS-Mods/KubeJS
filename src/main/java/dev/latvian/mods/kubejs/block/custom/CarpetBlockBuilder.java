package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CarpetBlock;

@ReturnsSelf
public class CarpetBlockBuilder extends ShapedBlockBuilder {
	public CarpetBlockBuilder(ResourceLocation i) {
		super(i, "_carpet");
		tagBoth(BlockTags.WOOL_CARPETS.location());
	}

	@Override
	public Block createObject() {
		return new CarpetBlock(createProperties());
	}

	@Override
	protected void generateBlockStateJson(VariantBlockStateGenerator bs) {
		var mod = newID("block/", "").toString();
		bs.variant("", (v) -> v.model(mod));
	}

	@Override
	protected void generateBlockModelJsons(AssetJsonGenerator generator) {
		var texture = textures.get("texture").getAsString();

		generator.blockModel(id, m -> {
			m.parent("minecraft:block/carpet");
			m.texture("wool", texture);
		});
	}

	public CarpetBlockBuilder texture(String texture) {
		return (CarpetBlockBuilder) textureAll(texture);
	}
}
