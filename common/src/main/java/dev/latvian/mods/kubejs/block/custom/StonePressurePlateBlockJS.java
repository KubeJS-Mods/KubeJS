package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import net.minecraft.world.level.block.PressurePlateBlock;

public class StonePressurePlateBlockJS extends PressurePlateBlock implements CustomBlockJS {
	public StonePressurePlateBlockJS(Properties properties) {
		super(Sensitivity.MOBS, properties);
	}

	@Override
	public void generateAssets(BlockBuilder builder, AssetJsonGenerator generator) {
		generator.blockState(builder.id, bs -> {
			bs.variant("powered=true", v -> v.model(builder.newID("block/", "_down").toString()));
			bs.variant("powered=false", v -> v.model(builder.newID("block/", "_up").toString()));
		});

		final String texture = builder.textures.get("texture").getAsString();

		generator.blockModel(builder.newID("", "_down"), m -> {
			m.parent("minecraft:block/pressure_plate_down");
			m.texture("texture", texture);
		});

		generator.blockModel(builder.newID("", "_up"), m -> {
			m.parent("minecraft:block/pressure_plate_up");
			m.texture("texture", texture);
		});

		generator.itemModel(builder.itemBuilder.id, m -> m.parent(builder.newID("block/", "_up").toString()));
	}
}
