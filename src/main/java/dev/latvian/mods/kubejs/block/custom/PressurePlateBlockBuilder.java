package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.util.ID;
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

	private static final ResourceLocation MODEL = ResourceLocation.withDefaultNamespace("block/pressure_plate_up");
	private static final ResourceLocation PRESSED_MODEL = ResourceLocation.withDefaultNamespace("block/pressure_plate_down");

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
		return new PressurePlateBlock(behaviour, createProperties());
	}

	@Override
	protected void generateBlockState(VariantBlockStateGenerator bs) {
		bs.variant("powered=false", v -> v.model(id.withPath(ID.BLOCK)));
		bs.variant("powered=true", v -> v.model(newID("block/", "_down")));
	}

	@Override
	protected void generateBlockModels(KubeAssetGenerator generator) {
		generator.blockModel(id, m -> {
			m.parent(MODEL);
			m.texture("texture", baseTexture);
		});

		generator.blockModel(newID("", "_down"), m -> {
			m.parent(PRESSED_MODEL);
			m.texture("texture", baseTexture);
		});
	}
}