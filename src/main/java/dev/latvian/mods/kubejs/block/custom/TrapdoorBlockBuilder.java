package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.block.BlockRenderType;
import dev.latvian.mods.kubejs.client.ModelGenerator;
import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
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

	public transient BlockSetType behaviour;

	public TrapdoorBlockBuilder(ResourceLocation i) {
		super(i);
		renderType(BlockRenderType.CUTOUT);
		noValidSpawns(true);
		notSolid();
		tagBoth(TRAPDOOR_TAGS);
		texture("texture", newID("block/", "").toString());
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
	protected void generateBlockStateJson(VariantBlockStateGenerator bs) {
		var modelOpen = newID("block/", "_open").toString();
		var modelBottom = newID("block/", "_bottom").toString();
		var modelTop = newID("block/", "_top").toString();

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
	protected void generateBlockModelJsons(KubeAssetGenerator generator) {
		var texture = textures.get("texture").getAsString();

		generator.blockModel(newID("", "_bottom"), m -> {
			m.parent("minecraft:block/template_trapdoor_bottom");
			m.texture("texture", texture);
		});

		generator.blockModel(newID("", "_top"), m -> {
			m.parent("minecraft:block/template_trapdoor_top");
			m.texture("texture", texture);
		});

		generator.blockModel(newID("", "_open"), m -> {
			m.parent("minecraft:block/template_trapdoor_open");
			m.texture("texture", texture);
		});
	}

	@Override
	protected void generateItemModelJson(ModelGenerator m) {
		m.parent(newID("block/", "_bottom").toString());
	}
}