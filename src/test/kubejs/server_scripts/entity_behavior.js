// Category 3 - entity behavior. On spawn, the script acts on the entity through the injected *KJS
// bindings, branching on entity type: the zombie is given a held item (LivingEntityKJS), the cow is
// moved 5 blocks south / +Z (EntityKJS). EntityBehaviorTests spawns both and verifies Java-side.

EntityEvents.spawned('minecraft:zombie', event => {
	event.entity.setMainHandItem(Item.of('minecraft:iron_sword'));
	TestRuntime.pass('entity.behavior.zombie');
});

EntityEvents.spawned('minecraft:cow', event => {
	let cow = event.entity;
	cow.setPosition(cow.x, cow.y, cow.z + 5);
	TestRuntime.pass('entity.behavior.cow');
});
