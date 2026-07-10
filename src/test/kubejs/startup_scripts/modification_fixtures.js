// Startup fixtures for ModificationTests, from the wiki item/block modification tutorials. Both are
// startup events. The ender_pearl max-stack change and the stone hardness change are asserted
// Java-side; the marker (which survives per-test clears via passStartup) confirms the block
// modification handler ran.

ItemEvents.modification(event => {
	// The wiki's `item.fireResistant = true` is omitted: on 26.1 fireResistant takes a damage-type
	// holder, not a boolean, so the old form throws.
	event.modify('minecraft:ender_pearl', item => {
		item.maxStackSize = 64;
		item.rarity = 'UNCOMMON';
	});
});

BlockEvents.modification(event => {
	event.modify('minecraft:stone', block => {
		block.destroySpeed = 0.1;
	});

	TestRuntime.passStartup('modification.block.wiki');
});
