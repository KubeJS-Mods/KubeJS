package dev.latvian.mods.kubejs;

import dev.latvian.mods.kubejs.block.BlockBrokenEventJS;
import dev.latvian.mods.kubejs.block.BlockLeftClickedEventJS;
import dev.latvian.mods.kubejs.block.BlockModificationEventJS;
import dev.latvian.mods.kubejs.block.BlockPlacedEventJS;
import dev.latvian.mods.kubejs.block.BlockRightClickedEventJS;
import dev.latvian.mods.kubejs.block.DetectorBlockEventJS;
import dev.latvian.mods.kubejs.client.ClientEventJS;
import dev.latvian.mods.kubejs.client.DebugInfoEventJS;
import dev.latvian.mods.kubejs.client.GenerateClientAssetsEventJS;
import dev.latvian.mods.kubejs.client.painter.screen.PaintScreenEventJS;
import dev.latvian.mods.kubejs.command.CommandRegistryEventJS;
import dev.latvian.mods.kubejs.entity.CheckLivingEntitySpawnEventJS;
import dev.latvian.mods.kubejs.entity.EntitySpawnedEventJS;
import dev.latvian.mods.kubejs.entity.LivingEntityDeathEventJS;
import dev.latvian.mods.kubejs.entity.LivingEntityHurtEventJS;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.StartupEventJS;
import dev.latvian.mods.kubejs.item.FoodEatenEventJS;
import dev.latvian.mods.kubejs.item.ItemCraftedEventJS;
import dev.latvian.mods.kubejs.item.ItemDroppedEventJS;
import dev.latvian.mods.kubejs.item.ItemEntityInteractedEventJS;
import dev.latvian.mods.kubejs.item.ItemLeftClickedEventJS;
import dev.latvian.mods.kubejs.item.ItemModelPropertiesEventJS;
import dev.latvian.mods.kubejs.item.ItemModificationEventJS;
import dev.latvian.mods.kubejs.item.ItemPickedUpEventJS;
import dev.latvian.mods.kubejs.item.ItemRightClickedEmptyEventJS;
import dev.latvian.mods.kubejs.item.ItemRightClickedEventJS;
import dev.latvian.mods.kubejs.item.ItemSmeltedEventJS;
import dev.latvian.mods.kubejs.item.ItemTooltipEventJS;
import dev.latvian.mods.kubejs.item.custom.ItemArmorTierRegistryEventJS;
import dev.latvian.mods.kubejs.item.custom.ItemToolTierRegistryEventJS;
import dev.latvian.mods.kubejs.level.ExplosionEventJS;
import dev.latvian.mods.kubejs.level.SimpleLevelEventJS;
import dev.latvian.mods.kubejs.level.gen.AddWorldgenEventJS;
import dev.latvian.mods.kubejs.level.gen.RemoveWorldgenEventJS;
import dev.latvian.mods.kubejs.net.NetworkEventJS;
import dev.latvian.mods.kubejs.player.ChestEventJS;
import dev.latvian.mods.kubejs.player.InventoryChangedEventJS;
import dev.latvian.mods.kubejs.player.InventoryEventJS;
import dev.latvian.mods.kubejs.player.PlayerAdvancementEventJS;
import dev.latvian.mods.kubejs.player.PlayerChatEventJS;
import dev.latvian.mods.kubejs.player.SimplePlayerEventJS;
import dev.latvian.mods.kubejs.recipe.AfterRecipesLoadedEventJS;
import dev.latvian.mods.kubejs.recipe.CompostableRecipesEventJS;
import dev.latvian.mods.kubejs.recipe.RecipeTypeRegistryEventJS;
import dev.latvian.mods.kubejs.recipe.RecipesEventJS;
import dev.latvian.mods.kubejs.recipe.special.SpecialRecipeSerializerManager;
import dev.latvian.mods.kubejs.script.data.DataPackEventJS;
import dev.latvian.mods.kubejs.server.CommandEventJS;
import dev.latvian.mods.kubejs.server.CustomCommandEventJS;
import dev.latvian.mods.kubejs.server.ServerEventJS;

public interface KubeJSEvents {
	EventGroup STARTUP_GROUP = EventGroup.of("StartupEvents");
	EventHandler STARTUP_INIT = STARTUP_GROUP.startup("init", () -> StartupEventJS.class).legacy("init");
	EventHandler STARTUP_POST_INIT = STARTUP_GROUP.startup("postInit", () -> StartupEventJS.class).legacy("postinit");

	EventGroup CLIENT_GROUP = EventGroup.of("ClientEvents");
	EventHandler CLIENT_INIT = CLIENT_GROUP.client("init", () -> ClientEventJS.class).legacy("client.init");
	EventHandler CLIENT_LOGGED_IN = CLIENT_GROUP.client("loggedIn", () -> ClientEventJS.class).legacy("client.logged_in");
	EventHandler CLIENT_LOGGED_OUT = CLIENT_GROUP.client("loggedOut", () -> ClientEventJS.class).legacy("client.logged_out");
	EventHandler CLIENT_TICK = CLIENT_GROUP.client("tick", () -> ClientEventJS.class).legacy("client.tick");
	EventHandler CLIENT_PAINTER_UPDATED = CLIENT_GROUP.client("painterUpdated", () -> ClientEventJS.class).legacy("client.painter_updated");
	EventHandler CLIENT_DEBUG_LEFT = CLIENT_GROUP.client("leftDebugInfo", () -> DebugInfoEventJS.class).legacy("client.debug_info.left");
	EventHandler CLIENT_DEBUG_RIGHT = CLIENT_GROUP.client("rightDebugInfo", () -> DebugInfoEventJS.class).legacy("client.debug_info.right");
	EventHandler CLIENT_PAINT_SCREEN = CLIENT_GROUP.client("paintScreen", () -> PaintScreenEventJS.class).legacy("client.paint_screen");

	EventGroup REGISTRY_GROUP = EventGroup.of("RegistryEvents");

	EventGroup DATAGEN_GROUP = EventGroup.of("DatagenEvents");
	EventHandler DATAGEN_LOW_DATA = DATAGEN_GROUP.server("lowPriorityData", () -> DataPackEventJS.class).legacy("server.datapack.low_priority");
	EventHandler DATAGEN_HIGH_DATA = DATAGEN_GROUP.server("highPriorityData", () -> DataPackEventJS.class).legacy("server.datapack.high_priority");
	// add low assets
	EventHandler DATAGEN_HIGH_ASSETS = DATAGEN_GROUP.client("highPriorityAssets", () -> GenerateClientAssetsEventJS.class).legacy("client.generate_assets");

	EventGroup SERVER_GROUP = EventGroup.of("ServerEvents");
	EventHandler SERVER_LOAD = SERVER_GROUP.server("loaded", () -> ServerEventJS.class).legacy("server.load");
	EventHandler SERVER_UNLOAD = SERVER_GROUP.server("unloaded", () -> ServerEventJS.class).legacy("server.unload");
	EventHandler SERVER_TICK = SERVER_GROUP.server("tick", () -> ServerEventJS.class).legacy("server.tick");
	EventHandler SERVER_COMMAND_REGISTRY = SERVER_GROUP.server("commandRegistry", () -> CommandRegistryEventJS.class).legacy("command.registry");
	EventHandler SERVER_COMMAND = SERVER_GROUP.server("command", () -> CommandEventJS.class).cancelable().legacy("command.run");
	EventHandler SERVER_CUSTOM_COMMAND = SERVER_GROUP.server("customCommand", () -> CustomCommandEventJS.class).cancelable().legacy("server.custom_command");
	EventHandler SERVER_RECIPES = SERVER_GROUP.server("recipes", () -> RecipesEventJS.class).legacy("recipes");
	EventHandler SERVER_RECIPES_AFTER_LOADED = SERVER_GROUP.server("afterRecipes", () -> AfterRecipesLoadedEventJS.class).legacy("recipes.after_loaded");
	EventHandler SERVER_SPECIAL_RECIPES = SERVER_GROUP.server("specialRecipeSerializers", () -> SpecialRecipeSerializerManager.class).legacy("recipes.serializer.special.flag");
	EventHandler SERVER_COMPOSTABLE = SERVER_GROUP.server("compostables", () -> CompostableRecipesEventJS.class).legacy("recipes.compostables");
	EventHandler SERVER_RECIPE_TYPE_REGISTRY = SERVER_GROUP.server("recipeTypeRegistry", () -> RecipeTypeRegistryEventJS.class).legacy("recipes.type_registry");

	EventGroup LEVEL_GROUP = EventGroup.of("LevelEvents");
	EventHandler LEVEL_LOAD = LEVEL_GROUP.server("loaded", () -> SimpleLevelEventJS.class).legacy("level.load");
	EventHandler LEVEL_UNLOAD = LEVEL_GROUP.server("unloaded", () -> SimpleLevelEventJS.class).legacy("level.unload");
	EventHandler LEVEL_TICK = LEVEL_GROUP.server("tick", () -> SimpleLevelEventJS.class).legacy("level.tick");
	EventHandler LEVEL_EXPLOSION_PRE = LEVEL_GROUP.server("preExplosion", () -> ExplosionEventJS.Pre.class).cancelable().legacy("level.explosion.pre");
	EventHandler LEVEL_EXPLOSION_POST = LEVEL_GROUP.server("postExplosion", () -> ExplosionEventJS.Post.class).legacy("level.explosion.post");

	EventGroup WORLDGEN_GROUP = EventGroup.of("WorldgenEvents");
	EventHandler WORLDGEN_ADD = WORLDGEN_GROUP.startup("add", () -> AddWorldgenEventJS.class).legacy("worldgen.add");
	EventHandler WORLDGEN_REMOVE = WORLDGEN_GROUP.startup("remove", () -> RemoveWorldgenEventJS.class).legacy("worldgen.remove");

	EventGroup NETWORK_GROUP = EventGroup.of("NetworkEvents");
	EventHandler NETWORK_FROM_SERVER = NETWORK_GROUP.server("fromServer", () -> NetworkEventJS.class).cancelable().legacy("player.data_from_server");
	EventHandler NETWORK_FROM_CLIENT = NETWORK_GROUP.client("fromClient", () -> NetworkEventJS.class).cancelable().legacy("player.data_from_client");

	EventGroup ITEM_GROUP = EventGroup.of("ItemEvents");
	EventHandler ITEM_MODIFICATION = ITEM_GROUP.startup("modification", () -> ItemModificationEventJS.class).legacy("item.modification");
	EventHandler ITEM_TOOL_TIER_REGISTRY = ITEM_GROUP.startup("toolTierRegistry", () -> ItemToolTierRegistryEventJS.class).legacy("item.registry.tool_tiers");
	EventHandler ITEM_ARMOR_TIER_REGISTRY = ITEM_GROUP.startup("armorTierRegistry", () -> ItemArmorTierRegistryEventJS.class).legacy("item.registry.armor_tiers");
	EventHandler ITEM_RIGHT_CLICKED = ITEM_GROUP.server("rightClicked", () -> ItemRightClickedEventJS.class).cancelable().legacy("item.right_click");
	EventHandler ITEM_PICKED_UP = ITEM_GROUP.server("pickedUp", () -> ItemPickedUpEventJS.class).legacy("item.pickup").cancelable();
	EventHandler ITEM_DROPPED = ITEM_GROUP.server("dropped", () -> ItemDroppedEventJS.class).legacy("item.toss").cancelable();
	EventHandler ITEM_ENTITY_INTERACTED = ITEM_GROUP.server("entityInteracted", () -> ItemEntityInteractedEventJS.class).cancelable().legacy("item.entity_interact");
	EventHandler ITEM_CRAFTED = ITEM_GROUP.server("crafted", () -> ItemCraftedEventJS.class).legacy("item.crafted");
	EventHandler ITEM_SMELTED = ITEM_GROUP.server("smelted", () -> ItemSmeltedEventJS.class).legacy("item.smelted");
	EventHandler ITEM_FOOD_EATEN = ITEM_GROUP.server("foodEaten", () -> FoodEatenEventJS.class).cancelable().legacy("item.food_eaten");
	EventHandler ITEM_RIGHT_CLICKED_EMPTY = ITEM_GROUP.client("rightClickedEmpty", () -> ItemRightClickedEmptyEventJS.class).legacy("item.right_click_empty");
	EventHandler ITEM_LEFT_CLICKED = ITEM_GROUP.client("leftClicked", () -> ItemLeftClickedEventJS.class).legacy("item.left_click");
	EventHandler ITEM_TOOLTIP = ITEM_GROUP.client("tooltip", () -> ItemTooltipEventJS.class).legacy("item.tooltip");
	EventHandler ITEM_MODEL_PROPERTIES = ITEM_GROUP.startup("modelProperties", () -> ItemModelPropertiesEventJS.class).legacy("item.model_properties");

	EventGroup BLOCK_GROUP = EventGroup.of("BlockEvents");
	EventHandler BLOCK_MODIFICATION = BLOCK_GROUP.startup("modification", () -> BlockModificationEventJS.class).legacy("block.modification");
	EventHandler BLOCK_RIGHT_CLICKED = BLOCK_GROUP.server("rightClicked", () -> BlockRightClickedEventJS.class).cancelable().legacy("block.right_click");
	EventHandler BLOCK_LEFT_CLICKED = BLOCK_GROUP.server("leftClicked", () -> BlockLeftClickedEventJS.class).cancelable().legacy("block.left_click");
	EventHandler BLOCK_PLACED = BLOCK_GROUP.server("placed", () -> BlockPlacedEventJS.class).cancelable().legacy("block.place");
	EventHandler BLOCK_BROKEN = BLOCK_GROUP.server("broken", () -> BlockBrokenEventJS.class).cancelable().legacy("block.break");
	EventHandler BLOCK_DETECTOR_CHANGED = BLOCK_GROUP.server("detectorChanged", () -> DetectorBlockEventJS.class).legacy("block.detector");
	EventHandler BLOCK_DETECTOR_POWERED = BLOCK_GROUP.server("detectorPowered", () -> DetectorBlockEventJS.class).legacy("block.detector.powered");
	EventHandler BLOCK_DETECTOR_UNPOWERED = BLOCK_GROUP.server("detectorUnpowered", () -> DetectorBlockEventJS.class).legacy("block.detector.unpowered");

	EventGroup ENTITY_GROUP = EventGroup.of("EntityEvents");
	EventHandler ENTITY_DEATH = ENTITY_GROUP.server("death", () -> LivingEntityDeathEventJS.class).cancelable().legacy("entity.death");
	EventHandler ENTITY_HURT = ENTITY_GROUP.server("hurt", () -> LivingEntityHurtEventJS.class).cancelable().legacy("entity.hurt");
	EventHandler ENTITY_CHECK_SPAWN = ENTITY_GROUP.server("checkSpawn", () -> CheckLivingEntitySpawnEventJS.class).cancelable().legacy("entity.check_spawn");
	EventHandler ENTITY_SPAWNED = ENTITY_GROUP.server("spawned", () -> EntitySpawnedEventJS.class).cancelable().legacy("entity.spawned");

	EventGroup PLAYER_GROUP = EventGroup.of("PlayerEvents");
	EventHandler PLAYER_LOGGED_IN = PLAYER_GROUP.server("loggedIn", () -> SimplePlayerEventJS.class).legacy("player.logged_in");
	EventHandler PLAYER_LOGGED_OUT = PLAYER_GROUP.server("loggedOut", () -> SimplePlayerEventJS.class).legacy("player.logged_out");
	EventHandler PLAYER_TICK = PLAYER_GROUP.server("tick", () -> SimplePlayerEventJS.class).legacy("player.tick");
	EventHandler PLAYER_CHAT = PLAYER_GROUP.server("chat", () -> PlayerChatEventJS.class).cancelable().legacy("player.chat");
	EventHandler PLAYER_ADVANCEMENT = PLAYER_GROUP.server("advancement", () -> PlayerAdvancementEventJS.class).cancelable().legacy("player.advancement");
	EventHandler PLAYER_INVENTORY_OPENED = PLAYER_GROUP.server("inventoryOpened", () -> InventoryEventJS.class).legacy("player.inventory.opened");
	EventHandler PLAYER_INVENTORY_CLOSED = PLAYER_GROUP.server("inventoryClosed", () -> InventoryEventJS.class).legacy("player.inventory.closed");
	EventHandler PLAYER_INVENTORY_CHANGED = PLAYER_GROUP.server("inventoryChanged", () -> InventoryChangedEventJS.class).legacy("player.inventory.changed");
	EventHandler PLAYER_CHEST_OPENED = PLAYER_GROUP.server("chestOpened", () -> ChestEventJS.class).legacy("player.chest.opened");
	EventHandler PLAYER_CHEST_CLOSED = PLAYER_GROUP.server("chestClosed", () -> ChestEventJS.class).legacy("player.chest.closed");

	static void register() {
		STARTUP_GROUP.register();
		CLIENT_GROUP.register();
		REGISTRY_GROUP.register();
		DATAGEN_GROUP.register();
		SERVER_GROUP.register();
		LEVEL_GROUP.register();
		WORLDGEN_GROUP.register();
		NETWORK_GROUP.register();
		ITEM_GROUP.register();
		BLOCK_GROUP.register();
		ENTITY_GROUP.register();
		PLAYER_GROUP.register();
	}
}
