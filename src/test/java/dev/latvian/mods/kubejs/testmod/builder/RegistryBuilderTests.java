package dev.latvian.mods.kubejs.testmod.builder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.GameTest;

import static dev.latvian.mods.kubejs.testmod.GameAsserts.assertj;
import static org.assertj.core.api.Assertions.assertThat;

/// Integration for the wiki registry example scripts (`wiki_builders.js`): the item, block, creative
/// tab and mob effect all register, and one wiki-set property or tag is verified for each.
@ForEachTest(groups = "kubejs.builder")
public class RegistryBuilderTests {
	@GameTest
	@EmptyTemplate
	@TestHolder(value = "wiki_registry_builders", description = "The wiki registry examples register an item, block, creative tab and mob effect")
	static void wikiRegistryBuilders(final DynamicTest test) {
		test.onGameTest(helper -> {
			var itemId = Identifier.parse("kubejs:wiki_item");
			var glowId = Identifier.parse("kubejs:wiki_item_glow");
			var blockId = Identifier.parse("kubejs:wiki_block");
			var tabId = Identifier.parse("kubejs:wiki_tab");
			var effectId = Identifier.parse("kubejs:wiki_effect");

			var glowStack = new ItemStack(BuiltInRegistries.ITEM.getValue(glowId));
			var block = BuiltInRegistries.BLOCK.getValue(blockId);
			var mobEffects = helper.getLevel().registryAccess().lookupOrThrow(Registries.MOB_EFFECT);

			assertj(helper, () -> {
				assertThat(BuiltInRegistries.ITEM.containsKey(itemId)).as("wiki_item registered").isTrue();
				assertThat(glowStack.getMaxStackSize()).as("wiki_item_glow max stack size").isEqualTo(16);
				assertThat(BuiltInRegistries.BLOCK.containsKey(blockId)).as("wiki_block registered").isTrue();
				assertThat(block.defaultBlockState().is(BlockTags.MINEABLE_WITH_PICKAXE)).as("wiki_block mineable with pickaxe").isTrue();
				assertThat(BuiltInRegistries.CREATIVE_MODE_TAB.containsKey(tabId)).as("wiki_tab registered").isTrue();
				assertThat(mobEffects.get(ResourceKey.create(Registries.MOB_EFFECT, effectId)).isPresent()).as("wiki_effect registered").isTrue();
			});

			helper.succeed();
		});
	}
}
