package dev.latvian.mods.kubejs.testmod.kjs;

import dev.latvian.mods.kubejs.testmod.TestRuntime;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.GameTest;

/// Drives the events the `kjs_reads.js` fixture listens on, then verifies its assertions (that the
/// *KJS getters resolve to defined values) surfaced no failure.
@ForEachTest(groups = "kubejs.kjs.reads")
public class KjsReadTests {
	private static final BlockPos POS = new BlockPos(1, 2, 1);

	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "kjs_entity_reads", description = "EntityKJS/LivingEntityKJS getters resolve for a spawned entity")
	static void entityReads(final DynamicTest test) {
		test.onGameTest(helper -> helper.startSequence(() -> helper.makeTickingMockServerPlayerInCorner(GameType.SURVIVAL))
			.thenExecute(() -> TestRuntime.clear("kjs.entity.reads"))
			.thenExecute(() -> helper.spawn(EntityType.PIG, POS))
			.thenWaitUntil(() -> helper.assertTrue(TestRuntime.passed("kjs.entity.reads"), "entity read fixture did not run"))
			.thenExecute(() -> TestRuntime.verify("kjs.entity.reads"))
			.thenSucceed());
	}

	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "kjs_block_reads", description = "LevelBlock getters resolve for a broken block")
	static void blockReads(final DynamicTest test) {
		test.onGameTest(helper -> helper.startSequence(() -> helper.makeTickingMockServerPlayerInCorner(GameType.SURVIVAL))
			.thenExecute(() -> TestRuntime.clear("kjs.block.reads"))
			.thenExecute(() -> helper.setBlock(POS, Blocks.DIRT.defaultBlockState()))
			.thenExecute(player -> player.gameMode.destroyBlock(helper.absolutePos(POS)))
			.thenWaitUntil(() -> helper.assertTrue(TestRuntime.passed("kjs.block.reads"), "block read fixture did not run"))
			.thenExecute(() -> TestRuntime.verify("kjs.block.reads"))
			.thenSucceed());
	}

	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "kjs_item_reads", description = "ItemStackKJS getters resolve for a dropped item")
	static void itemReads(final DynamicTest test) {
		test.onGameTest(helper -> helper.startSequence(() -> helper.makeTickingMockServerPlayerInCorner(GameType.SURVIVAL))
			.thenExecute(() -> TestRuntime.clear("kjs.item.reads"))
			.thenExecute(player -> player.drop(new ItemStack(Items.DIAMOND), false))
			.thenWaitUntil(() -> helper.assertTrue(TestRuntime.passed("kjs.item.reads"), "item read fixture did not run"))
			.thenExecute(() -> TestRuntime.verify("kjs.item.reads"))
			.thenSucceed());
	}
}
