// Startup fixtures that exercise a wide variety of builder code paths (BlockBuilder, the custom
// block builders, ItemBuilder and FluidBuilder) so that game-test coverage records them. These
// blocks/items/fluids only need to register successfully; no events assert on them.

StartupEvents.registry('block', event => {
	// Basic block hitting a broad spread of BlockBuilder setters.
	event.create('cov_basic')
		.hardness(2.5)
		.resistance(6.0)
		.lightLevel(0.5)
		.opaque(false)
		.fullBlock(true)
		.requiresTool()
		.slipperiness(0.9)
		.speedFactor(1.1)
		.jumpFactor(0.8)
		.soundType('stone')
		.mapColor('stone')
		.noValidSpawns(true)
		.suffocating(false)
		.viewBlocking(false)
		.redstoneConductor(false)
		.transparent(true)
		.waterlogged()
		.box(0, 0, 0, 16, 8, 16)
		.tagBoth('minecraft:mineable/pickaxe')
		.randomTick(cb => {})
		.entityInside(cb => {})
		.steppedOn(cb => {})
		.fallenOn(cb => {})
		.afterFallenOn(cb => {})
		.exploded(cb => {})
		.rotateState(cb => {})
		.mirrorState(cb => {})
		.rightClick(cb => {})
		.defaultState(cb => {})
		.placementState(cb => {})
		.canBeReplaced(cb => true);

	// Helper methods and no-item / unbreakable paths.
	event.create('cov_basic_unbreakable')
		.unbreakable()
		.defaultTranslucent()
		.dynamicMapColor(state => 'stone')
		.noDrops()
		.noItem()
		.tagBlock('minecraft:mineable/axe');

	// Cutout helper, sound helpers, bounciness and drops-less item.
	event.create('cov_basic_cutout')
		.defaultCutout()
		.woodSoundType()
		.bounciness(0.5)
		.color(0x44FF88);

	// Block that customises its item representation.
	event.create('cov_basic_with_item')
		.grassSoundType()
		.item(i => i.maxStackSize(16).glow(true).rarity('epic').tooltip('A covered block'));

	// Block entity path (BasicKubeBlock.WithEntity).
	event.create('cov_basic_entity').blockEntity(be => be.serverTicking());

	// Custom block types.
	event.create('cov_slab', 'slab').hardness(2.0).requiresTool();
	event.create('cov_stairs', 'stairs').hardness(2.0).stoneSoundType();
	event.create('cov_fence', 'fence').hardness(2.0);
	event.create('cov_wall', 'wall').hardness(2.0).gravelSoundType();
	event.create('cov_fence_gate', 'fence_gate').hardness(2.0);
	event.create('cov_pressure_plate', 'pressure_plate').hardness(0.5).behaviour('birch').ticksToStayPressed(40);
	event.create('cov_button', 'button').behaviour('oak').ticksToStayPressed(30);
	event.create('cov_falling', 'falling').sandSoundType().dustColor(0x807C7B);
	event.create('cov_carpet', 'carpet').glassSoundType();
	event.create('cov_door', 'door').behaviour('spruce').wooden();
	event.create('cov_trapdoor', 'trapdoor').behaviour('oak');

	// Cardinal / pillar with custom shapes to exercise their shape-rotation code.
	event.create('cov_cardinal', 'cardinal').box(2, 0, 2, 14, 12, 14);
	event.create('cov_cardinal_entity', 'cardinal').blockEntity(be => be.serverTicking());
	event.create('cov_pillar', 'pillar').box(1, 0, 1, 15, 16, 15).cropSoundType();
	event.create('cov_pillar_entity', 'pillar').blockEntity(be => be.serverTicking());

	// Crop with a shaped age curve and grow callbacks.
	event.create('cov_crop', 'crop')
		.age(4, shapes => shapes.wheat())
		.noSeeds()
		.farmersCanPlant()
		.bonemeal(cb => 2)
		.survive(cb => true)
		.growTick(cb => 1.0);
});

StartupEvents.registry('item', event => {
	// Stackable item with fuel, container, food, glow, tooltip, tags.
	event.create('cov_item_stack')
		.maxStackSize(16)
		.glow(true)
		.tooltip('Coverage item')
		.fireResistant()
		.containerItem('minecraft:bucket')
		.burnTime(200)
		.food(4, 0.3)
		.disableRepair()
		.rarity('rare')
		.tag('minecraft:planks');

	// Durability item with a rich food builder.
	event.create('cov_item_durable')
		.maxDamage(250)
		.food(f => f.nutrition(6).saturation(0.6).alwaysEdible().fastToEat().eatSeconds(1.2))
		.unstackable();
});

StartupEvents.registry('fluid', event => {
	event.create('cov_fluid')
		.slopeFindDistance(3)
		.levelDecreasePerBlock(2)
		.explosionResistance(50)
		.tickRate(10)
		.tint(0x3F76E4)
		.bucketColor(0x3F76E4)
		.translucent()
		.type(t => {});

	event.create('cov_fluid_bare')
		.noBucket()
		.noBlock();
});
