package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import net.minecraft.world.level.block.SlabBlock;

public class SlabBlockJS extends SlabBlock implements CustomBlockJS {
	public SlabBlockJS(Properties properties) {
		super(properties);
	}

	@Override
	public void generateAssets(BlockBuilder builder, AssetJsonGenerator generator) {
		generator.blockState(builder.id, bs -> {
			bs.variant("type=double", v -> v.model(builder.newID("block/", "_double").toString()));
			bs.variant("type=bottom", v -> v.model(builder.newID("block/", "_bottom").toString()));
			bs.variant("type=top", v -> v.model(builder.newID("block/", "_top").toString()));
		});

		final var texture = builder.textures.get("texture").getAsString();

		generator.blockModel(builder.newID("", "_double"), m -> {
			m.parent("minecraft:block/cube_all");
			m.texture("all", texture);
		});

		generator.blockModel(builder.newID("", "_bottom"), m -> {
			m.parent("minecraft:block/slab");
			m.texture("bottom", texture);
			m.texture("top", texture);
			m.texture("side", texture);
		});

		generator.blockModel(builder.newID("", "_top"), m -> {
			m.parent("minecraft:block/slab_top");
			m.texture("bottom", texture);
			m.texture("top", texture);
			m.texture("side", texture);
		});

		generator.itemModel(builder.itemBuilder.id, m -> m.parent(builder.newID("block/", "_bottom").toString()));
	}
}
