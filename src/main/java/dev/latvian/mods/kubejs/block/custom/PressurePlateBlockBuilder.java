package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.client.ModelGenerator;
import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;

@ReturnsSelf
public class PressurePlateBlockBuilder extends ShapedBlockBuilder {
	public static final ResourceLocation[] PRESSURE_PLATE_TAGS = {
		BlockTags.PRESSURE_PLATES.location(),
	};

	public transient BlockSetType behaviour;

	public PressurePlateBlockBuilder(ResourceLocation i) {
		super(i, "_pressure_plate");
		noCollision();
		tagBoth(PRESSURE_PLATE_TAGS);
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
		// TODO: Sensitivity is part of BlockSetType now
		return new PressurePlateBlock(behaviour, createProperties());
	}

	@Override
	protected void generateBlockStateJson(VariantBlockStateGenerator bs) {
		bs.variant("powered=true", v -> v.model(newID("block/", "_down").toString()));
		bs.variant("powered=false", v -> v.model(newID("block/", "_up").toString()));
	}

	@Override
	protected void generateBlockModelJsons(KubeAssetGenerator generator) {
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