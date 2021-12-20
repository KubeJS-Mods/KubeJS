package dev.latvian.mods.kubejs.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class DetectorBlock extends Block {
	private final String detectorId;

	public DetectorBlock(String i) {
		super(Properties.copy(Blocks.BEDROCK));
		detectorId = i;
		registerDefaultState(stateDefinition.any().setValue(BlockStateProperties.POWERED, false));
	}

	@Override
	public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
		if (!level.isClientSide) {
			var p = !blockState.getValue(BlockStateProperties.POWERED);

			if (p == level.hasNeighborSignal(blockPos)) {
				level.setBlock(blockPos, blockState.setValue(BlockStateProperties.POWERED, p), 2);
				new DetectorBlockEventJS(detectorId, level, blockPos, p).post("block.detector." + detectorId, p ? "powered" : "unpowered");
			}
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.POWERED);
	}
}
