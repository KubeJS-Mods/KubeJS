// Category 1/3 - builder integration. A block with a rightClick callback and a food item, each
// actually exercised by BuilderTests: the block is right-clicked (its callback must fire once) and
// the item's built food properties are asserted Java-side.

StartupEvents.registry('block', event => {
	event.create('builder_block').rightClick(e => TestRuntime.pass('builder.block.rightClicked'));
});

StartupEvents.registry('item', event => {
	event.create('builder_food').food(f => f.nutrition(6).saturation(0.6));
});
