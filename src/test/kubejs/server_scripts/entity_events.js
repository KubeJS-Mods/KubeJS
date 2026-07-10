// Game-test fixtures for EntityEventTests. Listeners are unfiltered; each @GameTest clears its
// marker right before driving the action, so the mock player's own spawn can't false-flag them.

EntityEvents.spawned(event => TestRuntime.pass('entity.spawned'));

EntityEvents.beforeHurt(event => TestRuntime.pass('entity.beforeHurt'));

EntityEvents.afterHurt(event => TestRuntime.pass('entity.afterHurt'));

EntityEvents.death(event => TestRuntime.pass('entity.death'));

EntityEvents.drops(event => TestRuntime.pass('entity.drops'));

EntityEvents.checkSpawn(event => TestRuntime.pass('entity.checkSpawn'));
