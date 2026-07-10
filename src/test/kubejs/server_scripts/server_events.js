// Game-test fixtures for ServerEventTests. tick is untargeted; command targets the command name.

ServerEvents.tick(event => TestRuntime.pass('server.tick'));

ServerEvents.command('help', event => {
	TestRuntime.check('server.command', () => {
		TestRuntime.assertThat(event.commandName).isEqualTo('help');
	});
});
