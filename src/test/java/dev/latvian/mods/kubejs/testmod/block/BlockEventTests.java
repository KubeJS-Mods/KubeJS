package dev.latvian.mods.kubejs.testmod.block;

import dev.latvian.mods.kubejs.core.GameRulesKJS;
import dev.latvian.mods.kubejs.testmod.TestRuntime;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.GameTest;

@ForEachTest(groups = "kubejs.block.event")
public class BlockEventTests {
	private static final BlockPos POS = new BlockPos(1, 2, 1);

	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "block_broken_dirt", description = "KubeJS BlockEvents.broken fires when a player breaks dirt")
	static void blockBrokenDirt(final DynamicTest test) {
		test.onGameTest(helper -> helper.startSequence(() -> helper.makeTickingMockServerPlayerInCorner(GameType.SURVIVAL))
			.thenExecute(() -> TestRuntime.clear("block.break.dirt"))
			.thenExecute(() -> helper.setBlock(POS, Blocks.DIRT.defaultBlockState()))
			.thenExecute(player -> player.gameMode.destroyBlock(helper.absolutePos(POS)))
			.thenWaitUntil(() -> helper.assertTrue(TestRuntime.passed("block.break.dirt"), "script did not report block.break.dirt"))
			.thenSucceed());
	}

	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "block_drops_dirt", description = "KubeJS BlockEvents.drops fires when a broken block drops items")
	static void blockDropsDirt(final DynamicTest test) {
		test.onGameTest(helper -> helper.startSequence(() -> helper.makeTickingMockServerPlayerInCorner(GameType.SURVIVAL))
			.thenExecute(() -> TestRuntime.clear("block.drops.dirt"))
			.thenExecute(() -> helper.setBlock(POS, Blocks.DIRT.defaultBlockState()))
			.thenExecute(player -> player.gameMode.destroyBlock(helper.absolutePos(POS)))
			.thenWaitUntil(() -> helper.assertTrue(TestRuntime.passed("block.drops.dirt"), "script did not report block.drops.dirt"))
			.thenSucceed());
	}

	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "block_placed", description = "KubeJS BlockEvents.placed fires when a player places a block")
	static void blockPlaced(final DynamicTest test) {
		test.onGameTest(helper -> helper.startSequence(() -> helper.makeTickingMockServerPlayerInCorner(GameType.SURVIVAL))
			.thenExecute(() -> TestRuntime.clear("block.placed"))
			.thenExecute(() -> helper.setBlock(POS, Blocks.STONE.defaultBlockState()))
			.thenExecute(player -> {
				var stack = new ItemStack(Items.OAK_PLANKS);
				player.setItemInHand(InteractionHand.MAIN_HAND, stack);
				var abs = helper.absolutePos(POS);
				var hit = new BlockHitResult(Vec3.atCenterOf(abs).add(0, 0.5, 0), Direction.UP, abs, false);
				player.gameMode.useItemOn(player, (ServerLevel) player.level(), stack, InteractionHand.MAIN_HAND, hit);
			})
			.thenWaitUntil(() -> helper.assertTrue(TestRuntime.passed("block.placed"), "script did not report block.placed"))
			.thenSucceed());
	}

	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "block_right_clicked", description = "KubeJS BlockEvents.rightClicked fires when a player right-clicks a block")
	static void blockRightClicked(final DynamicTest test) {
		test.onGameTest(helper -> helper.startSequence(() -> helper.makeTickingMockServerPlayerInCorner(GameType.SURVIVAL))
			.thenExecute(() -> TestRuntime.clear("block.rightClicked"))
			.thenExecute(() -> helper.setBlock(POS, Blocks.STONE.defaultBlockState()))
			.thenExecute(player -> {
				player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
				var abs = helper.absolutePos(POS);
				var hit = new BlockHitResult(Vec3.atCenterOf(abs).add(0, 0.5, 0), Direction.UP, abs, false);
				player.gameMode.useItemOn(player, (ServerLevel) player.level(), ItemStack.EMPTY, InteractionHand.MAIN_HAND, hit);
			})
			.thenWaitUntil(() -> helper.assertTrue(TestRuntime.passed("block.rightClicked"), "script did not report block.rightClicked"))
			.thenSucceed());
	}

	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "block_left_clicked", description = "KubeJS BlockEvents.leftClicked fires when a player starts breaking a block")
	static void blockLeftClicked(final DynamicTest test) {
		test.onGameTest(helper -> helper.startSequence(() -> helper.makeTickingMockServerPlayerInCorner(GameType.SURVIVAL))
			.thenExecute(() -> TestRuntime.clear("block.leftClicked"))
			.thenExecute(() -> helper.setBlock(POS, Blocks.STONE.defaultBlockState()))
			.thenExecute(player -> player.gameMode.handleBlockBreakAction(
				helper.absolutePos(POS),
				ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK,
				Direction.UP,
				player.level().getMaxY(),
				0))
			.thenWaitUntil(() -> helper.assertTrue(TestRuntime.passed("block.leftClicked"), "script did not report block.leftClicked"))
			.thenSucceed());
	}

	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "block_started_falling", description = "KubeJS BlockEvents.startedFalling fires when a falling block starts to fall")
	static void blockStartedFalling(final DynamicTest test) {
		test.onGameTest(helper -> helper.startSequence(() -> helper.makeTickingMockServerPlayerInCorner(GameType.SURVIVAL))
			.thenExecute(() -> TestRuntime.clear("block.startedFalling"))
			.thenExecute(() -> helper.setBlock(new BlockPos(1, 2, 1), Blocks.AIR.defaultBlockState()))
			.thenExecute(() -> helper.setBlock(new BlockPos(1, 3, 1), Blocks.SAND.defaultBlockState()))
			.thenIdle(4)
			.thenWaitUntil(() -> helper.assertTrue(TestRuntime.passed("block.startedFalling"), "script did not report block.startedFalling"))
			.thenSucceed());
	}

	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "block_stopped_falling", description = "KubeJS BlockEvents.stoppedFalling fires when a falling block lands")
	static void blockStoppedFalling(final DynamicTest test) {
		test.onGameTest(helper -> helper.startSequence(() -> helper.makeTickingMockServerPlayerInCorner(GameType.SURVIVAL))
			.thenExecute(() -> TestRuntime.clear("block.stoppedFalling"))
			.thenExecute(() -> helper.setBlock(new BlockPos(1, 2, 1), Blocks.AIR.defaultBlockState()))
			.thenExecute(() -> helper.setBlock(new BlockPos(1, 3, 1), Blocks.SAND.defaultBlockState()))
			.thenIdle(20)
			.thenWaitUntil(() -> helper.assertTrue(TestRuntime.passed("block.stoppedFalling"), "script did not report block.stoppedFalling"))
			.thenSucceed());
	}

	@GameTest
	@EmptyTemplate(value = "5x8x5", floor = true)
	@TestHolder(value = "block_farmland_trampled", description = "KubeJS BlockEvents.farmlandTrampled fires when an entity falls on farmland")
	static void blockFarmlandTrampled(final DynamicTest test) {
		test.onGameTest(helper -> helper.startSequence(() -> helper.makeTickingMockServerPlayerInCorner(GameType.SURVIVAL))
			.thenExecute(() -> TestRuntime.clear("block.farmlandTrampled"))
			.thenExecute(player -> ((GameRulesKJS) ((ServerLevel) player.level()).getGameRules()).kjs$set("mob_griefing", "true"))
			.thenExecute(() -> helper.setBlock(POS, Blocks.FARMLAND.defaultBlockState()))
			.thenExecute(() -> helper.spawnWithNoFreeWill(EntityType.GOAT, new BlockPos(1, 5, 1).getCenter()))
			.thenWaitUntil(() -> helper.assertTrue(TestRuntime.passed("block.farmlandTrampled"), "script did not report block.farmlandTrampled"))
			.thenSucceed());
	}

	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "block_random_tick", description = "KubeJS BlockEvents.randomTick fires when a block is randomly ticked")
	static void blockRandomTick(final DynamicTest test) {
		test.onGameTest(helper -> helper.startSequence(() -> helper.makeTickingMockServerPlayerInCorner(GameType.SURVIVAL))
			.thenExecute(() -> TestRuntime.clear("block.randomTick.dirt"))
			.thenExecute(() -> helper.setBlock(POS, Blocks.DIRT.defaultBlockState()))
			.thenExecute(player -> {
				var abs = helper.absolutePos(POS);
				var level = (ServerLevel) player.level();
				helper.getBlockState(POS).randomTick(level, abs, level.getRandom());
			})
			.thenWaitUntil(() -> helper.assertTrue(TestRuntime.passed("block.randomTick.dirt"), "script did not report block.randomTick.dirt"))
			.thenSucceed());
	}

	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "block_detector_powered", description = "KubeJS BlockEvents.detectorPowered/detectorChanged fire when a detector is powered")
	static void blockDetectorPowered(final DynamicTest test) {
		test.onGameTest(helper -> helper.startSequence(() -> helper.makeTickingMockServerPlayerInCorner(GameType.SURVIVAL))
			.thenExecute(() -> TestRuntime.clear("block.detector.powered", "block.detector.changed"))
			.thenExecute(() -> helper.setBlock(POS, detector()))
			.thenExecute(() -> helper.setBlock(new BlockPos(2, 2, 1), Blocks.REDSTONE_BLOCK.defaultBlockState()))
			.thenIdle(4)
			.thenWaitUntil(() -> helper.assertTrue(TestRuntime.passed("block.detector.powered"), "script did not report block.detector.powered"))
			.thenWaitUntil(() -> helper.assertTrue(TestRuntime.passed("block.detector.changed"), "script did not report block.detector.changed"))
			.thenSucceed());
	}

	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "block_detector_unpowered", description = "KubeJS BlockEvents.detectorUnpowered fires when a detector loses power")
	static void blockDetectorUnpowered(final DynamicTest test) {
		test.onGameTest(helper -> helper.startSequence(() -> helper.makeTickingMockServerPlayerInCorner(GameType.SURVIVAL))
			.thenExecute(() -> TestRuntime.clear("block.detector.unpowered"))
			.thenExecute(() -> helper.setBlock(POS, detector()))
			.thenExecute(() -> helper.setBlock(new BlockPos(2, 2, 1), Blocks.REDSTONE_BLOCK.defaultBlockState()))
			.thenIdle(4)
			.thenExecute(() -> helper.setBlock(new BlockPos(2, 2, 1), Blocks.AIR.defaultBlockState()))
			.thenIdle(4)
			.thenWaitUntil(() -> helper.assertTrue(TestRuntime.passed("block.detector.unpowered"), "script did not report block.detector.unpowered"))
			.thenSucceed());
	}

	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "block_entity_tick", description = "KubeJS BlockEvents.blockEntityTick fires while a ticking block entity is present")
	static void blockEntityTick(final DynamicTest test) {
		test.onGameTest(helper -> helper.startSequence(() -> helper.makeTickingMockServerPlayerInCorner(GameType.SURVIVAL))
			.thenExecute(() -> TestRuntime.clear("block.blockEntityTick"))
			.thenExecute(() -> helper.setBlock(POS, block("kubejs:test_ticker")))
			.thenIdle(5)
			.thenWaitUntil(() -> helper.assertTrue(TestRuntime.count("block.blockEntityTick") >= 1, "script did not report block.blockEntityTick"))
			.thenSucceed());
	}

	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "block_picked", description = "KubeJS BlockEvents.picked fires when a block is pick-blocked")
	static void blockPicked(final DynamicTest test) {
		test.onGameTest(helper -> helper.startSequence(() -> helper.makeTickingMockServerPlayerInCorner(GameType.SURVIVAL))
			.thenExecute(() -> TestRuntime.clear("block.picked"))
			.thenExecute(() -> helper.setBlock(POS, Blocks.STONE.defaultBlockState()))
			.thenExecute(player -> {
				var abs = helper.absolutePos(POS);
				helper.getBlockState(POS).getCloneItemStack(abs, (ServerLevel) player.level(), true, player);
			})
			.thenWaitUntil(() -> helper.assertTrue(TestRuntime.passed("block.picked"), "script did not report block.picked"))
			.thenSucceed());
	}

	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "block_modification", description = "KubeJS BlockEvents.modification fired during startup script load")
	static void blockModification(final DynamicTest test) {
		test.onGameTest(helper -> {
			helper.assertTrue(TestRuntime.passedStartup("block.modification"), "script did not report block.modification");
			helper.succeed();
		});
	}

	private static net.minecraft.world.level.block.state.BlockState detector() {
		return block("kubejs:test_detector");
	}

	private static net.minecraft.world.level.block.state.BlockState block(String id) {
		return BuiltInRegistries.BLOCK.getValue(Identifier.parse(id)).defaultBlockState();
	}
}
