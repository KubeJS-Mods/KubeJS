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

import static dev.latvian.mods.kubejs.testmod.GameAsserts.assertCount;
import static dev.latvian.mods.kubejs.testmod.GameAsserts.assertFired;
import static dev.latvian.mods.kubejs.testmod.GameAsserts.assertVerified;
import static dev.latvian.mods.kubejs.testmod.GameAsserts.assertj;
import static org.assertj.core.api.Assertions.assertThat;

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
			.thenWaitUntil(() -> assertFired(helper, "block.break.dirt"))
			.thenExecute(() -> assertCount(helper, "block.break.dirt", 1))
			.thenSucceed());
	}

	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "block_broken_asserts", description = "KubeJS BlockEvents.broken exposes the broken block and player to script assertions")
	static void blockBrokenAsserts(final DynamicTest test) {
		test.onGameTest(helper -> helper.startSequence(() -> helper.makeTickingMockServerPlayerInCorner(GameType.SURVIVAL))
			.thenExecute(() -> TestRuntime.clear("block.broken.assert"))
			.thenExecute(() -> helper.setBlock(POS, Blocks.DIRT.defaultBlockState()))
			.thenExecute(player -> player.gameMode.destroyBlock(helper.absolutePos(POS)))
			.thenWaitUntil(() -> assertFired(helper, "block.broken.assert"))
			.thenExecute(() -> assertVerified(helper, "block.broken.assert"))
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
			.thenWaitUntil(() -> assertFired(helper, "block.drops.dirt"))
			.thenExecute(() -> assertCount(helper, "block.drops.dirt", 1))
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
			.thenWaitUntil(() -> assertFired(helper, "block.placed"))
			.thenExecute(() -> assertCount(helper, "block.placed", 1))
			.thenSucceed());
	}

	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "block_placed_asserts", description = "KubeJS BlockEvents.placed exposes the placed block to script assertions")
	static void blockPlacedAsserts(final DynamicTest test) {
		test.onGameTest(helper -> helper.startSequence(() -> helper.makeTickingMockServerPlayerInCorner(GameType.SURVIVAL))
			.thenExecute(() -> TestRuntime.clear("block.placed.assert"))
			.thenExecute(() -> helper.setBlock(POS, Blocks.STONE.defaultBlockState()))
			.thenExecute(player -> {
				var stack = new ItemStack(Items.OAK_PLANKS);
				player.setItemInHand(InteractionHand.MAIN_HAND, stack);
				var abs = helper.absolutePos(POS);
				var hit = new BlockHitResult(Vec3.atCenterOf(abs).add(0, 0.5, 0), Direction.UP, abs, false);
				player.gameMode.useItemOn(player, (ServerLevel) player.level(), stack, InteractionHand.MAIN_HAND, hit);
			})
			.thenWaitUntil(() -> assertFired(helper, "block.placed.assert"))
			.thenExecute(() -> assertVerified(helper, "block.placed.assert"))
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
			.thenWaitUntil(() -> assertFired(helper, "block.rightClicked"))
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
			.thenWaitUntil(() -> assertFired(helper, "block.leftClicked"))
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
			.thenWaitUntil(() -> assertFired(helper, "block.startedFalling"))
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
			.thenWaitUntil(() -> assertFired(helper, "block.stoppedFalling"))
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
			.thenWaitUntil(() -> assertFired(helper, "block.farmlandTrampled"))
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
			.thenWaitUntil(() -> assertFired(helper, "block.randomTick.dirt"))
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
			.thenWaitUntil(() -> assertFired(helper, "block.detector.powered"))
			.thenWaitUntil(() -> assertFired(helper, "block.detector.changed"))
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
			.thenWaitUntil(() -> assertFired(helper, "block.detector.unpowered"))
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
			.thenWaitUntil(() -> assertFired(helper, "block.blockEntityTick"))
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
			.thenWaitUntil(() -> assertFired(helper, "block.picked"))
			.thenSucceed());
	}

	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "block_modification", description = "KubeJS BlockEvents.modification fired during startup script load")
	static void blockModification(final DynamicTest test) {
		test.onGameTest(helper -> {
			assertj(helper, () -> assertThat(TestRuntime.passedStartup("block.modification")).as("block.modification should have fired during startup").isTrue());
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
