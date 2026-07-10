package dev.latvian.mods.kubejs.testmod.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.GameTest;

import static dev.latvian.mods.kubejs.testmod.GameAsserts.assertFired;
import static dev.latvian.mods.kubejs.testmod.GameAsserts.assertVerified;
import static dev.latvian.mods.kubejs.testmod.GameAsserts.assertj;
import static org.assertj.core.api.Assertions.assertThat;

/// Integration for the wiki tutorials/tags example scripts (`tag_events.js`): the tag edits run at
/// datapack load, and their effect is asserted against the bound tags - a fresh item tag gains and
/// loses members and nests another tag, and a vanilla block tag gains a member.
@ForEachTest(groups = "kubejs.tag.event")
public class TagEventTests {
	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "tags_item_wiki", description = "The wiki item-tag example adds, removes and nests item tags")
	static void tagsItemWiki(final DynamicTest test) {
		test.onGameTest(helper -> {
			assertFired(helper, "tags.item.wiki");
			assertVerified(helper, "tags.item.wiki");

			var tag = TagKey.create(Registries.ITEM, Identifier.parse("kubejs:wiki_tag"));
			var parent = TagKey.create(Registries.ITEM, Identifier.parse("kubejs:wiki_parent"));

			assertj(helper, () -> {
				assertThat(new ItemStack(Items.DIAMOND).is(tag)).as("diamond added to wiki_tag").isTrue();
				assertThat(new ItemStack(Items.EMERALD).is(tag)).as("emerald removed from wiki_tag").isFalse();
				assertThat(new ItemStack(Items.DIAMOND).is(parent)).as("wiki_tag nested into wiki_parent").isTrue();
			});

			helper.succeed();
		});
	}

	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "tags_block_wiki", description = "The wiki block-tag example adds cobblestone to the climbable tag")
	static void tagsBlockWiki(final DynamicTest test) {
		test.onGameTest(helper -> {
			assertFired(helper, "tags.block.wiki");
			assertVerified(helper, "tags.block.wiki");

			assertj(helper, () -> assertThat(Blocks.COBBLESTONE.defaultBlockState().is(BlockTags.CLIMBABLE)).as("cobblestone made climbable").isTrue());

			helper.succeed();
		});
	}
}
