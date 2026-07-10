// Game-test fixtures for PlayerEventTests. tick is untargeted. (Stage/inventory/advancement events
// aren't cleanly drivable from a headless game test, so they're not covered here.)

PlayerEvents.tick(event => TestRuntime.pass('player.tick'));
