package dev.latvian.mods.kubejs.testmod.modification;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.GameTest;

import static dev.latvian.mods.kubejs.testmod.GameAsserts.assertj;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

/// Integration for the wiki item/block modification examples (`modification_fixtures.js`), both
/// startup events, each asserted by its effect: the ender pearl's max stack size and stone's per-state
/// destroy speed are the modified values. `block.destroySpeed` writes the block state's `destroySpeed`
/// field (read by `BlockState#getDestroySpeed`), not the block's `Properties#destroyTime`.
@ForEachTest(groups = "kubejs.modification")
public class ModificationTests {
	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "item_modification_wiki", description = "The wiki ItemEvents.modification example raises ender pearl max stack size")
	static void itemModificationWiki(final DynamicTest test) {
		test.onGameTest(helper -> {
			assertj(helper, () -> assertThat(new ItemStack(Items.ENDER_PEARL).getMaxStackSize()).as("ender pearl max stack size").isEqualTo(64));
			helper.succeed();
		});
	}

	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "block_modification_wiki", description = "The wiki BlockEvents.modification example lowers stone destroy speed")
	static void blockModificationWiki(final DynamicTest test) {
		test.onGameTest(helper -> {
			var destroySpeed = Blocks.STONE.defaultBlockState().getDestroySpeed(helper.getLevel(), BlockPos.ZERO);
			assertj(helper, () -> assertThat(destroySpeed).as("stone destroy speed").isCloseTo(0.1f, offset(0.001f)));
			helper.succeed();
		});
	}
}
