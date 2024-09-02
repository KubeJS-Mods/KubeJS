package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.bindings.event.BlockEvents;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class DetectorBlock extends Block {
	@ReturnsSelf
	public static class Builder extends BlockBuilder {
		private static final ResourceLocation OFF_MODEL = KubeJS.id("block/detector");
		private static final ResourceLocation ON_MODEL = KubeJS.id("block/detector_on");

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

			displayName(Component.literal("KubeJS Detector [" + detectorId + "]"));
		}

		public Builder detectorId(String id) {
			detectorId = id;
			displayName(Component.literal("KubeJS Detector [" + detectorId + "]"));
			return this;
		}

		@Override
		public Block createObject() {
			return new DetectorBlock(this);
		}

		@Override
		public void generateAssets(KubeAssetGenerator generator) {
			generator.blockState(id, bs -> {
				bs.simpleVariant("powered=false", OFF_MODEL);
				bs.simpleVariant("powered=true", ON_MODEL);
			});

			generator.itemModel(id, m -> m.parent(KubeJS.MOD_ID + ":block/detector"));
		}
	}

	private final Builder builder;

	public DetectorBlock(Builder b) {
		super(Properties.ofFullCopy(Blocks.BEDROCK));
		builder = b;
		registerDefaultState(stateDefinition.any().setValue(BlockStateProperties.POWERED, false));
	}

	@Override
	@Deprecated
	public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
		var p = !blockState.getValue(BlockStateProperties.POWERED);

		if (p == level.hasNeighborSignal(blockPos)) {
			level.setBlock(blockPos, blockState.setValue(BlockStateProperties.POWERED, p), 2);

			if (BlockEvents.DETECTOR_CHANGED.hasListeners(builder.detectorId) || (p ? BlockEvents.DETECTOR_POWERED : BlockEvents.DETECTOR_UNPOWERED).hasListeners(builder.detectorId)) {
				var e = new DetectorBlockKubeEvent(builder.detectorId, level, blockPos, p);
				BlockEvents.DETECTOR_CHANGED.post(level, builder.detectorId, e);

				if (p) {
					BlockEvents.DETECTOR_POWERED.post(level, builder.detectorId, e);
				} else {
					BlockEvents.DETECTOR_UNPOWERED.post(level, builder.detectorId, e);
				}
			}
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.POWERED);
	}
}
