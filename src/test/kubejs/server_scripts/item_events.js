// Game-test fixtures for ItemEventTests. Listeners are untargeted; each @GameTest clears its
// marker before driving the interaction that should fire the event.

ItemEvents.dropped(event => {
	TestRuntime.check('item.dropped', () => {
		TestRuntime.assertThat(event.item.id).isEqualTo('minecraft:diamond');
	});
});

ItemEvents.rightClicked(event => TestRuntime.pass('item.rightClicked'));

ItemEvents.entityInteracted(event => TestRuntime.pass('item.entityInteracted'));

ItemEvents.canPickUp(event => {
	TestRuntime.check('item.canPickUp', () => {
		TestRuntime.assertThat(event.item.id).isEqualTo('minecraft:diamond');
	});
});

ItemEvents.pickedUp(event => TestRuntime.pass('item.pickedUp'));
