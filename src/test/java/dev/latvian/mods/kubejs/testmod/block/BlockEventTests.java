package dev.latvian.mods.kubejs.testmod.block;

import dev.latvian.mods.kubejs.testmod.TestRuntime;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.GameTest;

@ForEachTest(groups = "kubejs.block.event")
public class BlockEventTests {
	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "block_broken_dirt", description = "KubeJS BlockEvents.broken fires when a player breaks dirt")
	static void blockBrokenDirt(final DynamicTest test) {
		test.onGameTest(helper -> helper.startSequence(() -> helper.makeTickingMockServerPlayerInCorner(GameType.SURVIVAL))
			.thenExecute(() -> TestRuntime.reset())
			.thenExecute(() -> helper.setBlock(1, 2, 1, Blocks.DIRT.defaultBlockState()))
			.thenExecute(player -> player.gameMode.destroyBlock(helper.absolutePos(new BlockPos(1, 2, 1))))
			.thenWaitUntil(() -> helper.assertTrue(TestRuntime.passed("block.break.dirt"), "script did not report block.break.dirt"))
			.thenSucceed());
	}
}
