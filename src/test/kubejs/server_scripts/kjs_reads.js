// Coverage fixtures for the vanilla-injected *KJS convenience getters (EntityKJS, LivingEntityKJS,
// PlayerKJS/ServerPlayerKJS, LevelBlock, ItemStackKJS). These read-only listeners piggy-back on the
// events the existing @GameTests already drive (entity spawns, block break/place, item drop/use), so
// every no-arg getter runs as its bean property is read. They report no markers and assert nothing;
// the event dispatcher isolates each listener, so a getter that throws for one entity type can't fail
// a paired test. Reads that need a script Context, an argument, or a specific entity capability are
// either skipped or guarded behind the relevant is* check.

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

BlockEvents.broken(event => readBlock(event.block));

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

ItemEvents.dropped(event => readItem(event.item));

ItemEvents.rightClicked(event => readItem(event.item));
