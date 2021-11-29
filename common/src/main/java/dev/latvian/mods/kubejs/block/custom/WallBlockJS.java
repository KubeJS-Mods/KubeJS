package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import net.minecraft.world.level.block.WallBlock;

public class WallBlockJS extends WallBlock implements CustomBlockJS {
	public WallBlockJS(Properties properties) {
		super(properties);
	}

	@Override
	public void generateAssets(BlockBuilder builder, AssetJsonGenerator generator) {
		generator.multipartState(builder.id, bs -> {
			String modPost = builder.newID("block/", "_post").toString();
			String modSide = builder.newID("block/", "_side").toString();
			String modSideTall = builder.newID("block/", "_side_tall").toString();

			bs.part("up=true", modPost);
			bs.part("north=low", p -> p.model(modSide).uvlock());
			bs.part("east=low", p -> p.model(modSide).uvlock().y(90));
			bs.part("south=low", p -> p.model(modSide).uvlock().y(180));
			bs.part("west=low", p -> p.model(modSide).uvlock().y(270));
			bs.part("north=tall", p -> p.model(modSideTall).uvlock());
			bs.part("east=tall", p -> p.model(modSideTall).uvlock().y(90));
			bs.part("south=tall", p -> p.model(modSideTall).uvlock().y(180));
			bs.part("west=tall", p -> p.model(modSideTall).uvlock().y(270));
		});

		final String texture = builder.textures.get("texture").getAsString();

		generator.blockModel(builder.newID("", "_post"), m -> {
			m.parent("minecraft:block/template_wall_post");
			m.texture("wall", texture);
		});

		generator.blockModel(builder.newID("", "_side"), m -> {
			m.parent("minecraft:block/template_wall_side");
			m.texture("wall", texture);
		});

		generator.blockModel(builder.newID("", "_side_tall"), m -> {
			m.parent("minecraft:block/template_wall_side_tall");
			m.texture("wall", texture);
		});

		generator.itemModel(builder.itemBuilder.id, m -> {
			m.parent("minecraft:block/wall_inventory");
			m.texture("wall", texture);
		});
	}

	// FIXME: fix connection
}
