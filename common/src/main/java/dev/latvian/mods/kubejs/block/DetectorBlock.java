package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSEvents;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class DetectorBlock extends Block {
	public static class Builder extends BlockBuilder {
		public transient String detectorId;

		public Builder(ResourceLocation i) {
			super(i);
			detectorId = (id.getNamespace().equals(KubeJS.MOD_ID) ? "" : (id.getNamespace() + ".")) + id.getPath().replace('/', '.');

			if (detectorId.endsWith("_detector")) {
				detectorId = detectorId.substring(0, detectorId.length() - 9);
			}

			if (detectorId.startsWith("detector_")) {
				detectorId = detectorId.substring(9);
			}

			displayName("KubeJS Detector [" + detectorId + "]");
		}

		public Builder detectorId(String id) {
			detectorId = id;
			displayName("KubeJS Detector [" + detectorId + "]");
			return this;
		}

		@Override
		public Block createObject() {
			return new DetectorBlock(this);
		}

		@Override
		public void generateAssetJsons(AssetJsonGenerator generator) {
			generator.blockState(id, bs -> {
				bs.variant("powered=false", "kubejs:block/detector");
				bs.variant("powered=true", "kubejs:block/detector_on");
			});

			generator.itemModel(id, m -> m.parent(KubeJS.MOD_ID + ":block/detector"));
		}
	}

	private final Builder builder;

	public DetectorBlock(Builder b) {
		super(Properties.copy(Blocks.BEDROCK));
		builder = b;
		registerDefaultState(stateDefinition.any().setValue(BlockStateProperties.POWERED, false));
	}

	@Override
	@Deprecated
	public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
		if (!level.isClientSide) {
			var p = !blockState.getValue(BlockStateProperties.POWERED);

			if (p == level.hasNeighborSignal(blockPos)) {
				level.setBlock(blockPos, blockState.setValue(BlockStateProperties.POWERED, p), 2);
				var e = new DetectorBlockEventJS(builder.detectorId, level, blockPos, p);
				KubeJSEvents.BLOCK_DETECTOR_CHANGED.post(builder.detectorId, e);

				if (p) {
					KubeJSEvents.BLOCK_DETECTOR_POWERED.post(builder.detectorId, e);
				} else {
					KubeJSEvents.BLOCK_DETECTOR_UNPOWERED.post(builder.detectorId, e);
				}
			}
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.POWERED);
	}
}
