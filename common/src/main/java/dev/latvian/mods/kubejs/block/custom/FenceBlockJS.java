package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import net.minecraft.world.level.block.FenceBlock;

public class FenceBlockJS extends FenceBlock implements CustomBlockJS {
	public FenceBlockJS(Properties properties) {
		super(properties);
	}

	@Override
	public void generateAssets(BlockBuilder builder, AssetJsonGenerator generator) {
		generator.multipartState(builder.id, bs -> {
			var modPost = builder.newID("block/", "_post").toString();
			var modSide = builder.newID("block/", "_side").toString();

			bs.part("", modPost);
			bs.part("north=true", p -> p.model(modSide).uvlock());
			bs.part("east=true", p -> p.model(modSide).uvlock().y(90));
			bs.part("south=true", p -> p.model(modSide).uvlock().y(180));
			bs.part("west=true", p -> p.model(modSide).uvlock().y(270));
		});

		final var texture = builder.textures.get("texture").getAsString();

		generator.blockModel(builder.newID("", "_post"), m -> {
			m.parent("minecraft:block/fence_post");
			m.texture("texture", texture);
		});

		generator.blockModel(builder.newID("", "_side"), m -> {
			m.parent("minecraft:block/fence_side");
			m.texture("texture", texture);
		});

		generator.itemModel(builder.itemBuilder.id, m -> {
			m.parent("minecraft:block/fence_inventory");
			m.texture("texture", texture);
		});
	}
}
