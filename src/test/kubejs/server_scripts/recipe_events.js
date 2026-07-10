// Game-test fixtures for RecipeEventTests. Runs the wiki tutorials/recipes example scripts inside a
// single ServerEvents.recipes handler wrapped in TestRuntime.check, so any failure at datapack load
// surfaces to the paired @GameTest. The added `kubejs:wiki_shaped` and the removed `minecraft:glowstone`
// recipes are asserted Java-side against the live RecipeManager. Outputs are vanilla so the wiki
// snippets resolve without needing registered KubeJS items.

ServerEvents.recipes(event => {
	TestRuntime.check('recipes.wiki', () => {
		event.shaped(Item.of('minecraft:stone', 3), [
			'A B',
			' C ',
			'B A'
		], {
			A: 'minecraft:andesite',
			B: 'minecraft:diorite',
			C: 'minecraft:granite'
		}).id('kubejs:wiki_shaped');

		event.shapeless(Item.of('minecraft:dandelion', 3), [
			'minecraft:bone_meal',
			'minecraft:yellow_dye',
			'3x minecraft:ender_pearl'
		]);

		event.smelting('3x minecraft:gravel', 'minecraft:stone');
		event.blasting('10x minecraft:iron_nugget', 'minecraft:iron_ingot');
		event.smoking('minecraft:tinted_glass', 'minecraft:glass').xp(0.35);
		event.campfireCooking('minecraft:torch', 'minecraft:stick', 0.35, 600);

		event.stonecutting('3x minecraft:stick', '#minecraft:planks');

		event.smithing(
			'minecraft:netherite_ingot',
			'minecraft:netherite_upgrade_smithing_template',
			'minecraft:iron_ingot',
			'minecraft:black_dye'
		);

		event.replaceInput({ input: 'minecraft:stick' }, 'minecraft:stick', Ingredient.of('#minecraft:saplings'));

		// Helper-function pattern from the wiki, with a vanilla output.
		const potting = (output, pottedInput) => {
			event.shaped(output, [
				'BIB',
				' B '
			], {
				B: 'minecraft:brick',
				I: pottedInput
			});
		};

		potting('minecraft:blast_furnace', 'minecraft:furnace');

		// Looping pattern from the wiki, with vanilla outputs.
		['oak', 'spruce', 'birch'].forEach(wood => {
			event.stonecutting(`4x minecraft:${wood}_button`, `minecraft:${wood}_planks`);
		});

		event.remove({ output: 'minecraft:stone_pickaxe' });
		event.remove({ output: '#minecraft:wool' });
		event.remove({ mod: 'farmersdelight' });
		event.remove({ id: 'minecraft:glowstone' });
	});
});
