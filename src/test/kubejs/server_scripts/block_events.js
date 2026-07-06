// Game-test fixtures for BlockEventTests. Each listener flags TestRuntime with a marker the
// paired @GameTest polls after driving the action that should fire the event.

BlockEvents.broken(event => {
	if (event.block.id === 'minecraft:dirt') {
		TestRuntime.pass('block.break.dirt');
	}
});

BlockEvents.drops(event => {
	if (event.block.id === 'minecraft:dirt') {
		TestRuntime.pass('block.drops.dirt');
	}
});

BlockEvents.placed(event => TestRuntime.pass('block.placed'));

BlockEvents.rightClicked(event => TestRuntime.pass('block.rightClicked'));

BlockEvents.leftClicked(event => TestRuntime.pass('block.leftClicked'));

BlockEvents.startedFalling(event => TestRuntime.pass('block.startedFalling'));

BlockEvents.stoppedFalling(event => TestRuntime.pass('block.stoppedFalling'));

BlockEvents.farmlandTrampled(event => TestRuntime.pass('block.farmlandTrampled'));

BlockEvents.randomTick('minecraft:dirt', event => TestRuntime.pass('block.randomTick.dirt'));

BlockEvents.detectorChanged('test', event => TestRuntime.pass('block.detector.changed'));

BlockEvents.detectorPowered('test', event => TestRuntime.pass('block.detector.powered'));

BlockEvents.detectorUnpowered('test', event => TestRuntime.pass('block.detector.unpowered'));

BlockEvents.blockEntityTick('kubejs:test_ticker', event => TestRuntime.pass('block.blockEntityTick'));

BlockEvents.picked('minecraft:stone', event => TestRuntime.pass('block.picked'));
