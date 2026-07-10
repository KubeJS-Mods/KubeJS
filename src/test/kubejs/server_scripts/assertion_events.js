// Game-test fixtures exercising TestRuntime's typed assertions. Each listener asserts on the event
// object inside a check() block; the paired @GameTest clears the marker, drives the action, then
// verify()s the captured result so a wrong value fails with an AssertJ message, not a bare timeout.

BlockEvents.broken(event => {
	TestRuntime.check('block.broken.assert', () => {
		TestRuntime.assertThat(event.block).hasId('minecraft:dirt');
		TestRuntime.assertThat(event).hasPlayer();
	});
});

BlockEvents.placed(event => {
	TestRuntime.check('block.placed.assert', () => {
		TestRuntime.assertThat(event.block).hasId('minecraft:oak_planks');
	});
});

EntityEvents.death(event => {
	TestRuntime.check('entity.death.assert', () => {
		TestRuntime.assertThat(event).hasEntityType('minecraft:pig').hasNoPlayer();
		TestRuntime.assertThat(event.source).isNotNull();
	});
});
