// Game-test fixtures for TagEventTests, from the wiki tutorials/tags examples. A fresh kubejs:wiki_tag
// exercises add / remove / tag-in-tag on the item registry, and the block example makes cobblestone
// climbable. Both are wrapped in TestRuntime.check so a load-time failure reaches the paired @GameTest,
// and the tag membership is asserted Java-side.

ServerEvents.tags('item', event => {
	TestRuntime.check('tags.item.wiki', () => {
		event.add('kubejs:wiki_tag', 'minecraft:diamond');
		event.add('kubejs:wiki_tag', 'minecraft:emerald');
		event.remove('kubejs:wiki_tag', 'minecraft:emerald');
		event.add('kubejs:wiki_parent', '#kubejs:wiki_tag');
	});
});

ServerEvents.tags('block', event => {
	TestRuntime.check('tags.block.wiki', () => {
		event.add('minecraft:climbable', 'minecraft:cobblestone');
	});
});
