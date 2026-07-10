package dev.latvian.mods.kubejs.testmod.builder;

import dev.latvian.mods.kubejs.testmod.TestRuntime;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.GameTest;

import static dev.latvian.mods.kubejs.testmod.GameAsserts.assertCount;
import static dev.latvian.mods.kubejs.testmod.GameAsserts.assertFired;
import static dev.latvian.mods.kubejs.testmod.GameAsserts.assertj;
import static org.assertj.core.api.Assertions.assertThat;

/// Integration for the builders registered in `builder_integrated.js`: a builder callback fires when
/// its block is used, and a builder-configured food component is applied to the registered item.
@ForEachTest(groups = "kubejs.builder")
public class BuilderTests {
	private static final BlockPos POS = new BlockPos(1, 2, 1);

	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "builder_block_right_click", description = "A BlockBuilder rightClick callback fires when the block is right-clicked")
	static void builderBlockRightClick(final DynamicTest test) {
		test.onGameTest(helper -> helper.startSequence(() -> helper.makeTickingMockServerPlayerInCorner(GameType.SURVIVAL))
			.thenExecute(() -> TestRuntime.clear("builder.block.rightClicked"))
			.thenExecute(() -> helper.setBlock(POS, BuiltInRegistries.BLOCK.getValue(Identifier.parse("kubejs:builder_block")).defaultBlockState()))
			.thenExecute(player -> {
				player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
				var abs = helper.absolutePos(POS);
				var hit = new BlockHitResult(Vec3.atCenterOf(abs).add(0, 0.5, 0), Direction.UP, abs, false);
				player.gameMode.useItemOn(player, (ServerLevel) player.level(), ItemStack.EMPTY, InteractionHand.MAIN_HAND, hit);
			})
			.thenWaitUntil(() -> assertFired(helper, "builder.block.rightClicked"))
			.thenExecute(() -> assertCount(helper, "builder.block.rightClicked", 1))
			.thenSucceed());
	}

	@GameTest
	@EmptyTemplate
	@TestHolder(value = "builder_item_food", description = "An ItemBuilder food component is applied to the registered item")
	static void builderItemFood(final DynamicTest test) {
		test.onGameTest(helper -> {
			var item = BuiltInRegistries.ITEM.getValue(Identifier.parse("kubejs:builder_food"));
			var food = new ItemStack(item).get(DataComponents.FOOD);
			assertj(helper, () -> {
				assertThat(food).as("builder_food food component").isNotNull();
				assertThat(food.nutrition()).as("builder_food nutrition").isEqualTo(6);
			});
			helper.succeed();
		});
	}
}
