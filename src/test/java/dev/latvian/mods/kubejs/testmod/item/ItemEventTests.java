package dev.latvian.mods.kubejs.testmod.item;

import dev.latvian.mods.kubejs.testmod.TestRuntime;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.GameTest;

import static dev.latvian.mods.kubejs.testmod.GameAsserts.assertCount;
import static dev.latvian.mods.kubejs.testmod.GameAsserts.assertFired;
import static dev.latvian.mods.kubejs.testmod.GameAsserts.assertVerified;

@ForEachTest(groups = "kubejs.item.event")
public class ItemEventTests {
	private static final BlockPos POS = new BlockPos(1, 2, 1);

	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "item_dropped", description = "KubeJS ItemEvents.dropped fires when a player drops an item")
	static void itemDropped(final DynamicTest test) {
		test.onGameTest(helper -> helper.startSequence(() -> helper.makeTickingMockServerPlayerInCorner(GameType.SURVIVAL))
			.thenExecute(() -> TestRuntime.clear("item.dropped"))
			.thenExecute(player -> player.drop(new ItemStack(Items.DIAMOND), false))
			.thenWaitUntil(() -> assertFired(helper, "item.dropped"))
			.thenExecute(() -> assertCount(helper, "item.dropped", 1))
			.thenExecute(() -> assertVerified(helper, "item.dropped"))
			.thenSucceed());
	}

	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "item_right_clicked", description = "KubeJS ItemEvents.rightClicked fires when a player right-clicks with an item")
	static void itemRightClicked(final DynamicTest test) {
		test.onGameTest(helper -> helper.startSequence(() -> helper.makeTickingMockServerPlayerInCorner(GameType.SURVIVAL))
			.thenExecute(() -> TestRuntime.clear("item.rightClicked"))
			.thenExecute(player -> {
				var stack = new ItemStack(Items.STICK);
				player.setItemInHand(InteractionHand.MAIN_HAND, stack);
				player.gameMode.useItem(player, player.level(), stack, InteractionHand.MAIN_HAND);
			})
			.thenWaitUntil(() -> assertFired(helper, "item.rightClicked"))
			.thenExecute(() -> assertCount(helper, "item.rightClicked", 1))
			.thenSucceed());
	}

	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "item_entity_interacted", description = "KubeJS ItemEvents.entityInteracted fires when a player interacts with an entity")
	static void itemEntityInteracted(final DynamicTest test) {
		test.onGameTest(helper -> helper.startSequence(() -> helper.makeTickingMockServerPlayerInCorner(GameType.SURVIVAL))
			.thenExecute(() -> TestRuntime.clear("item.entityInteracted"))
			.thenExecute(player -> {
				player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.STICK));
				var pig = helper.spawn(EntityType.PIG, POS);
				player.interactOn(pig, InteractionHand.MAIN_HAND, pig.position());
			})
			.thenWaitUntil(() -> assertFired(helper, "item.entityInteracted"))
			.thenSucceed());
	}

	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "item_picked_up", description = "KubeJS ItemEvents.canPickUp/pickedUp fire when a player picks up an item")
	static void itemPickedUp(final DynamicTest test) {
		test.onGameTest(helper -> helper.startSequence(() -> helper.makeTickingMockServerPlayerInCorner(GameType.SURVIVAL))
			.thenExecute(() -> TestRuntime.clear("item.canPickUp", "item.pickedUp"))
			.thenExecute(player -> {
				var abs = helper.absolutePos(POS);
				var entity = new ItemEntity((ServerLevel) player.level(), abs.getX() + 0.5, abs.getY(), abs.getZ() + 0.5, new ItemStack(Items.DIAMOND));
				entity.setNoPickUpDelay();
				player.level().addFreshEntity(entity);
				entity.playerTouch(player);
			})
			.thenWaitUntil(() -> assertFired(helper, "item.canPickUp"))
			.thenExecute(() -> assertVerified(helper, "item.canPickUp"))
			.thenWaitUntil(() -> assertFired(helper, "item.pickedUp"))
			.thenSucceed());
	}
}
