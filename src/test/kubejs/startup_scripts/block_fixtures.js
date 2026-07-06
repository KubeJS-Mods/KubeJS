// Startup fixtures for BlockEventTests: the custom blocks that back the events which only fire for
// a KubeJS-registered block (a detector and a ticking block entity), plus the startup-time
// modification listener.

StartupEvents.registry('block', event => {
	event.create('test_detector', 'detector').detectorId('test');
	event.create('test_ticker').blockEntity(be => be.serverTicking());
});

BlockEvents.modification(event => TestRuntime.passStartup('block.modification'));
