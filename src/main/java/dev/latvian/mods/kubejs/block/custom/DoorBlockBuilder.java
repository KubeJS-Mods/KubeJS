package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.block.BlockRenderType;
import dev.latvian.mods.kubejs.client.ModelGenerator;
import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.List;
import java.util.Map;

@ReturnsSelf
public class DoorBlockBuilder extends ShapedBlockBuilder {
	public static final ResourceLocation[] TRAPDOOR_TAGS = {
		BlockTags.TRAPDOORS.location(),
	};

	public transient BlockSetType behaviour;

	public DoorBlockBuilder(ResourceLocation i) {
		super(i);
		renderType(BlockRenderType.CUTOUT);
		noValidSpawns(true);
		notSolid();
		tagBoth(TRAPDOOR_TAGS);
		texture("top", newID("block/", "_top").toString());
		texture("bottom", newID("block/", "_bottom").toString());
		hardness(3F);
		behaviour = BlockSetType.OAK;
	}

	public DoorBlockBuilder behaviour(BlockSetType wt) {
		behaviour = wt;
		return this;
	}

	@Override
	public Block createObject() {
		return new DoorBlock(behaviour, createProperties());
	}

	@Override
	protected void generateBlockStateJson(VariantBlockStateGenerator bs) {
		var modelMap = Map.of(
			DoubleBlockHalf.UPPER, Map.of(
				DoorHingeSide.RIGHT, Map.of(
					Boolean.FALSE, newID("block/", "_top_right").toString(),
					Boolean.TRUE, newID("block/", "_top_right_open").toString()
				),
				DoorHingeSide.LEFT, Map.of(
					Boolean.FALSE, newID("block/", "_top_left").toString(),
					Boolean.TRUE, newID("block/", "_top_left_open").toString()
				)
			),
			DoubleBlockHalf.LOWER, Map.of(
				DoorHingeSide.RIGHT, Map.of(
					Boolean.FALSE, newID("block/", "_bottom_right").toString(),
					Boolean.TRUE, newID("block/", "_bottom_right_open").toString()
				),
				DoorHingeSide.LEFT, Map.of(
					Boolean.FALSE, newID("block/", "_bottom_left").toString(),
					Boolean.TRUE, newID("block/", "_bottom_left_open").toString()
				)
			)
		);

		var rotationMap = Map.of(
			Direction.EAST, Map.of(
				DoorHingeSide.RIGHT, Map.of(
					Boolean.FALSE, 0,
					Boolean.TRUE, 270
				),
				DoorHingeSide.LEFT, Map.of(
					Boolean.FALSE, 0,
					Boolean.TRUE, 90
				)
			),
			Direction.NORTH, Map.of(
				DoorHingeSide.RIGHT, Map.of(
					Boolean.FALSE, 270,
					Boolean.TRUE, 180
				),
				DoorHingeSide.LEFT, Map.of(
					Boolean.FALSE, 270,
					Boolean.TRUE, 0
				)
			),
			Direction.SOUTH, Map.of(
				DoorHingeSide.RIGHT, Map.of(
					Boolean.FALSE, 90,
					Boolean.TRUE, 0
				),
				DoorHingeSide.LEFT, Map.of(
					Boolean.FALSE, 90,
					Boolean.TRUE, 180
				)
			),
			Direction.WEST, Map.of(
				DoorHingeSide.RIGHT, Map.of(
					Boolean.FALSE, 180,
					Boolean.TRUE, 90
				),
				DoorHingeSide.LEFT, Map.of(
					Boolean.FALSE, 180,
					Boolean.TRUE, 270
				)
			)
		);

		var halfValues = DoubleBlockHalf.values();
		var openValues = List.of(Boolean.TRUE, Boolean.FALSE);
		var facingValues = BlockStateProperties.HORIZONTAL_FACING.getPossibleValues();
		var hingeValues = DoorHingeSide.values();

		for (var half : halfValues) {
			for (var open : openValues) {
				for (var facing : facingValues) {
					for (var hinge : hingeValues) {
						bs.variant("facing=" + facing.getSerializedName() + ",half=" + half.getSerializedName() + ",hinge=" + hinge.getSerializedName() + ",open=" + open, v -> {
							v.model(modelMap.get(half).get(hinge).get(open)).y(rotationMap.get(facing).get(hinge).get(open));
						});
					}
				}
			}
		}
	}

	@Override
	protected void generateBlockModelJsons(KubeAssetGenerator generator) {
		var topTexture = textures.get("top").getAsString();
		var bottomTexture = textures.get("bottom").getAsString();

		for (var type : List.of(
			"top_right",
			"top_right_open",
			"top_left",
			"top_left_open",
			"bottom_right",
			"bottom_right_open",
			"bottom_left",
			"bottom_left_open"
		)) {
			generator.blockModel(newID("", "_" + type), m -> {
				m.parent("minecraft:block/door_" + type);
				m.texture("top", topTexture);
				m.texture("bottom", bottomTexture);
			});
		}
	}

	@Override
	protected void generateItemModelJson(ModelGenerator m) {
		m.parent("minecraft:item/generated");
		m.texture("layer0", newID("item/", "").toString());
	}
}