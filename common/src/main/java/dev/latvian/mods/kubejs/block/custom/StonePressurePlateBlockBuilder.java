package dev.latvian.mods.kubejs.block.custom;

/*
public class StonePressurePlateBlockBuilder extends ShapedBlockBuilder {
	public StonePressurePlateBlockBuilder(ResourceLocation i) {
		super(i, "_stone_pressure_plate", "_pressure_plate");
		noCollision();
		tagBoth(BlockTags.PRESSURE_PLATES.location());
		tagBoth(BlockTags.STONE_PRESSURE_PLATES.location());
	}

	@Override
	public Block createObject() {
		return new PressurePlateBlock(PressurePlateBlock.Sensitivity.MOBS, createProperties());
	}

	@Override
	protected void generateBlockStateJson(VariantBlockStateGenerator bs) {
		bs.variant("powered=true", v -> v.model(newID("block/", "_down").toString()));
		bs.variant("powered=false", v -> v.model(newID("block/", "_up").toString()));
	}

	@Override
	protected void generateBlockModelJsons(AssetJsonGenerator generator) {
		var texture = textures.get("texture").getAsString();

		generator.blockModel(newID("", "_down"), m -> {
			m.parent("minecraft:block/pressure_plate_down");
			m.texture("texture", texture);
		});

		generator.blockModel(newID("", "_up"), m -> {
			m.parent("minecraft:block/pressure_plate_up");
			m.texture("texture", texture);
		});
	}

	@Override
	protected void generateItemModelJson(ModelGenerator m) {
		m.parent(newID("block/", "_up").toString());
	}
}
*/