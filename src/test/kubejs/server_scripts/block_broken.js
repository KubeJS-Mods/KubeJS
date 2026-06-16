// Game-test fixture for BlockBrokenGameTest.
BlockEvents.broken(event => {
	if (event.block.id === 'minecraft:dirt') {
		TestRuntime.pass('block.break.dirt');
	}
});
