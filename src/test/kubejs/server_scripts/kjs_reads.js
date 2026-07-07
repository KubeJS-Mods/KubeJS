// Coverage + sanity fixtures for the vanilla-injected *KJS convenience getters (EntityKJS,
// LivingEntityKJS, PlayerKJS/ServerPlayerKJS, LevelBlock, ItemStackKJS). Each listener piggy-backs
// on an event the existing @GameTests already drive: it first reads a broad set of getters (so every
// property is exercised for coverage), then asserts - inside a TestRuntime.check block that the paired
// KjsReadTests verify - that the always-present properties actually resolve (not null / not JS
// undefined). Conditionally-null getters (profile on a non-player, block-entity on a plain block, ...)
// are read for coverage but not asserted.

function readEntity(e) {
	e.level;
	e.server;
	e.type;
	e.username;
	e.name;
	e.displayName;
	e.profile;
	e.self;
	e.player;
	e.serverPlayer;
	e.clientPlayer;
	e.item;
	e.frame;
	e.living;
	e.monster;
	e.animal;
	e.ambientCreature;
	e.waterCreature;
	e.peacefulCreature;
	e.motionX;
	e.motionY;
	e.motionZ;
	e.passengers;
	e.teamId;
	e.teamName;
	e.onScoreboardTeam;
	e.facing;
	e.block;
	e.nbt;
	e.scriptType;
	e.id;
	e.rawPersistentData;
}

function readLiving(e) {
	e.undead;
	e.potionEffects;
	e.mainHandItem;
	e.offHandItem;
	e.headArmorItem;
	e.chestArmorItem;
	e.legsArmorItem;
	e.feetArmorItem;
	e.totalMovementSpeed;
	e.defaultMovementSpeed;
}

function readPlayer(e) {
	e.fake;
	e.stages;
	e.stats;
	e.miningBlock;
	e.inventory;
	e.selectedSlot;
	e.mouseItem;
	e.openInventory;
	e.foodLevel;
	e.saturation;
	e.xp;
	e.xpLevel;
	e.reachDistance;
}

function readServerPlayer(e) {
	e.serverPlayer;
	e.op;
	e.spawnLocation;
}

EntityEvents.spawned(event => {
	let e = event.entity;
	readEntity(e);

	if (e.living) {
		readLiving(e);
	}

	if (e.player) {
		readPlayer(e);
	}

	if (e.serverPlayer) {
		readServerPlayer(e);
	}

	TestRuntime.check('kjs.entity.reads', () => {
		TestRuntime.assertDefined('entity.type', e.type);
		TestRuntime.assertDefined('entity.name', e.name);
		TestRuntime.assertDefined('entity.displayName', e.displayName);
		TestRuntime.assertDefined('entity.username', e.username);
		TestRuntime.assertDefined('entity.level', e.level);
		TestRuntime.assertDefined('entity.block', e.block);
		TestRuntime.assertDefined('entity.facing', e.facing);
		TestRuntime.assertDefined('entity.nbt', e.nbt);
		TestRuntime.assertDefined('entity.passengers', e.passengers);
		TestRuntime.assertDefined('entity.scriptType', e.scriptType);
		TestRuntime.assertDefined('entity.motionX', e.motionX);
		TestRuntime.assertDefined('entity.motionY', e.motionY);
		TestRuntime.assertDefined('entity.motionZ', e.motionZ);
	});
});

function readBlock(b) {
	b.id;
	b.block;
	b.dimension;
	b.dimensionKey;
	b.x;
	b.y;
	b.z;
	b.centerX;
	b.centerY;
	b.centerZ;
	b.blockState;
	b.properties;
	b.entity;
	b.entityId;
	b.entityData;
	b.light;
	b.skyLight;
	b.blockLight;
	b.canSeeSky;
	b.inventory;
	b.item;
	b.drops;
	b.biomeId;
	b.playersInRadius;
	b.up;
	b.down;
	b.north;
	b.south;
	b.east;
	b.west;
	b.toBlockStateString();
}

function assertBlock(b) {
	TestRuntime.check('kjs.block.reads', () => {
		TestRuntime.assertDefined('block.id', b.id);
		TestRuntime.assertDefined('block.block', b.block);
		TestRuntime.assertDefined('block.blockState', b.blockState);
		TestRuntime.assertDefined('block.properties', b.properties);
		TestRuntime.assertDefined('block.dimension', b.dimension);
		TestRuntime.assertDefined('block.dimensionKey', b.dimensionKey);
		TestRuntime.assertDefined('block.biomeId', b.biomeId);
		TestRuntime.assertDefined('block.x', b.x);
		TestRuntime.assertDefined('block.y', b.y);
		TestRuntime.assertDefined('block.z', b.z);
		TestRuntime.assertDefined('block.centerX', b.centerX);
		TestRuntime.assertDefined('block.up', b.up);
		TestRuntime.assertDefined('block.down', b.down);
		TestRuntime.assertDefined('block.north', b.north);
		TestRuntime.assertDefined('block.toBlockStateString', b.toBlockStateString());
	});
}

BlockEvents.broken(event => {
	readBlock(event.block);
	assertBlock(event.block);
});

BlockEvents.placed(event => readBlock(event.block));

function readItem(item) {
	item.id;
	item.mod;
	item.block;
	item.idLocation;
	item.key;
	item.registry;
	item.registryId;
	item.enchantments;
	item.harvestSpeed;
	item.typeData;
	item.asHolder();
	item.asIngredient();
}

ItemEvents.dropped(event => {
	readItem(event.item);
	TestRuntime.check('kjs.item.reads', () => {
		TestRuntime.assertDefined('item.id', event.item.id);
		TestRuntime.assertDefined('item.mod', event.item.mod);
		TestRuntime.assertDefined('item.idLocation', event.item.idLocation);
		TestRuntime.assertDefined('item.enchantments', event.item.enchantments);
	});
});

ItemEvents.rightClicked(event => readItem(event.item));
