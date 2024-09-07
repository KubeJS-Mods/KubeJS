package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.block.BlockRenderType;
import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;

import java.util.List;

@ReturnsSelf
public class TrapdoorBlockBuilder extends ShapedBlockBuilder {
	public static final ResourceLocation[] TRAPDOOR_TAGS = {
		BlockTags.TRAPDOORS.location(),
	};

	private static final ResourceLocation MODEL = ResourceLocation.withDefaultNamespace("block/template_trapdoor_bottom");
	private static final ResourceLocation TOP_MODEL = ResourceLocation.withDefaultNamespace("block/template_trapdoor_top");
	private static final ResourceLocation OPEN_MODEL = ResourceLocation.withDefaultNamespace("block/template_trapdoor_open");

	public transient BlockSetType behaviour;

	public TrapdoorBlockBuilder(ResourceLocation i) {
		super(i);
		renderType(BlockRenderType.CUTOUT);
		noValidSpawns(true);
		notSolid();
		tagBoth(TRAPDOOR_TAGS);
		baseTexture = id.withPath(ID.BLOCK).toString();
		hardness(3F);
		behaviour = BlockSetType.OAK;
	}

	public TrapdoorBlockBuilder behaviour(BlockSetType wt) {
		behaviour = wt;
		return this;
	}

	@Override
	public Block createObject() {
		return new TrapDoorBlock(behaviour, createProperties());
	}

	@Override
	protected void generateBlockState(VariantBlockStateGenerator bs) {
		var modelBottom = id.withPath(ID.BLOCK);
		var modelTop = newID("block/", "_top");
		var modelOpen = newID("block/", "_open");

		var halfValues = Half.values();
		var openValues = List.of(Boolean.TRUE, Boolean.FALSE);
		var facingValues = BlockStateProperties.HORIZONTAL_FACING.getPossibleValues();

		for (var half : halfValues) {
			for (var open : openValues) {
				for (var facing : facingValues) {
					bs.variant("facing=" + facing.getSerializedName() + ",half=" + half.getSerializedName() + ",open=" + open, v -> {
						var m = v.model(open ? modelOpen : half == Half.TOP ? modelTop : modelBottom);

						if (open) {
							m.y(switch (facing) {
								case EAST -> 90;
								case SOUTH -> 180;
								case WEST -> 270;
								default -> 0;
							});
						}
					});
				}
			}
		}
	}

	@Override
	protected void generateBlockModels(KubeAssetGenerator generator) {
		generator.blockModel(id, m -> {
			m.parent(MODEL);
			m.texture("texture", baseTexture);
		});

		generator.blockModel(newID("", "_top"), m -> {
			m.parent(TOP_MODEL);
			m.texture("texture", baseTexture);
		});

		generator.blockModel(newID("", "_open"), m -> {
			m.parent(OPEN_MODEL);
			m.texture("texture", baseTexture);
		});
	}
}