// Game-test fixtures for LevelEventTests. Listeners are untargeted (the events support but don't
// require a dimension target); each @GameTest clears its marker before driving the action.

LevelEvents.tick(event => TestRuntime.pass('level.tick'));

LevelEvents.beforeExplosion(event => {
	TestRuntime.check('level.beforeExplosion', () => {
		TestRuntime.assertThat(event.size).isGreaterThan(0);
	});
});

LevelEvents.afterExplosion(event => {
	TestRuntime.check('level.afterExplosion', () => {
		TestRuntime.assertThat(event.affectedBlocks).isNotEmpty();
	});
});
