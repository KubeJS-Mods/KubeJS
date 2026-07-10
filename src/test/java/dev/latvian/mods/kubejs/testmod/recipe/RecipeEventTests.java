package dev.latvian.mods.kubejs.testmod.recipe;

import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.GameTest;

import java.util.stream.Collectors;

import static dev.latvian.mods.kubejs.testmod.GameAsserts.assertFired;
import static dev.latvian.mods.kubejs.testmod.GameAsserts.assertVerified;
import static dev.latvian.mods.kubejs.testmod.GameAsserts.assertj;
import static org.assertj.core.api.Assertions.assertThat;

/// Integration for the wiki tutorials/recipes example scripts (`recipe_events.js`): the whole example
/// runs at datapack load, and its effect is asserted against the live recipe manager - the added
/// shaped recipe is present, a control vanilla recipe survives, and the removed recipe is gone.
@ForEachTest(groups = "kubejs.recipe.event")
public class RecipeEventTests {
	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "recipes_wiki", description = "The wiki recipe example scripts load and take effect on the recipe manager")
	static void recipesWiki(final DynamicTest test) {
		test.onGameTest(helper -> {
			assertFired(helper, "recipes.wiki");
			assertVerified(helper, "recipes.wiki");

			var ids = helper.getLevel().getServer().getRecipeManager().getRecipes().stream()
				.map(holder -> holder.id().identifier().toString())
				.collect(Collectors.toSet());

			assertj(helper, () -> {
				assertThat(ids).as("added wiki shaped recipe").contains("kubejs:wiki_shaped");
				assertThat(ids).as("control vanilla recipe").contains("minecraft:diamond_block");
				assertThat(ids).as("removed glowstone recipe").doesNotContain("minecraft:glowstone");
			});

			helper.succeed();
		});
	}
}
