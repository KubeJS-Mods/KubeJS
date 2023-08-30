package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.client.ModelGenerator;
import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class PressurePlateBlockBuilder extends ShapedBlockBuilder {
	public transient BlockSetType behaviour;

	public PressurePlateBlockBuilder(ResourceLocation i) {
		super(i, "_pressure_plate");
		noCollision();
		tagBoth(BlockTags.PRESSURE_PLATES.location());
		// tagBoth(BlockTags.WOODEN_PRESSURE_PLATES.location());
		behaviour = BlockSetType.OAK;
	}

	public PressurePlateBlockBuilder behaviour(BlockSetType wt) {
		behaviour = wt;
		return this;
	}

	public PressurePlateBlockBuilder behaviour(String wt) {
		for (var type : BlockSetType.values().toList()) {
			if (type.name().equals(wt)) {
				behaviour = type;
				return this;
			}
		}

		return this;
	}

	@Override
	public Block createObject() {
		return new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, createProperties(), behaviour);
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