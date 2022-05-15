package dev.latvian.mods.kubejs;

/**
 * @author LatvianModder
 */
public interface KubeJSEvents {
	String INIT = "init";
	String POSTINIT = "postinit";
	String LOADED = "loaded";
	String COMMAND_REGISTRY = "command.registry";
	String COMMAND_RUN = "command.run";

	String CLIENT_INIT = "client.init";
	String CLIENT_DEBUG_INFO_LEFT = "client.debug_info.left";
	String CLIENT_DEBUG_INFO_RIGHT = "client.debug_info.right";
	String CLIENT_LOGGED_IN = "client.logged_in";
	String CLIENT_LOGGED_OUT = "client.logged_out";
	String CLIENT_TICK = "client.tick";
	String CLIENT_PAINT_SCREEN = "client.paint_screen";
	String CLIENT_PAINT_WORLD = "client.paint_world";
	String CLIENT_PAINTER_UPDATED = "client.painter_updated";
	String CLIENT_GENERATE_ASSETS = "client.generate_assets";

	String SERVER_LOAD = "server.load";
	String SERVER_UNLOAD = "server.unload";
	String SERVER_TICK = "server.tick";
	String SERVER_DATAPACK_HIGH_PRIORITY = "server.datapack.high_priority";
	String SERVER_DATAPACK_LOW_PRIORITY = "server.datapack.low_priority";
	String SERVER_CUSTOM_COMMAND = "server.custom_command";
	String RECIPES = "recipes";
	String RECIPES_AFTER_LOAD = "recipes.after_load";
	String RECIPES_SERIALIZER_SPECIAL_FLAG = "recipes.serializer.special.flag";
	String RECIPES_COMPOSTABLES = "recipes.compostables";
	String RECIPES_TYPE_REGISTRY = "recipes.type_registry";
	String WORLDGEN_ADD = "worldgen.add";
	String WORLDGEN_REMOVE = "worldgen.remove";

	String LEVEL_LOAD = "level.load";
	String LEVEL_UNLOAD = "level.unload";
	String LEVEL_TICK = "level.tick";
	String LEVEL_EXPLOSION_PRE = "level.explosion.pre";
	String LEVEL_EXPLOSION_POST = "level.explosion.post";

	String PLAYER_LOGGED_IN = "player.logged_in";
	String PLAYER_LOGGED_OUT = "player.logged_out";
	String PLAYER_TICK = "player.tick";
	String PLAYER_DATA_FROM_SERVER = "player.data_from_server";
	String PLAYER_DATA_FROM_CLIENT = "player.data_from_client";
	String PLAYER_CHAT = "player.chat";
	String PLAYER_ADVANCEMENT = "player.advancement";
	String PLAYER_INVENTORY_OPENED = "player.inventory.opened";
	String PLAYER_INVENTORY_CLOSED = "player.inventory.closed";
	String PLAYER_INVENTORY_CHANGED = "player.inventory.changed";
	String PLAYER_CHEST_OPENED = "player.chest.opened";
	String PLAYER_CHEST_CLOSED = "player.chest.closed";
	String PLAYER_CHECK_LOGIN = "player.check_login";

	String ENTITY_DEATH = "entity.death";
	String ENTITY_HURT = "entity.hurt";
	String ENTITY_DROPS = "entity.drops";
	String ENTITY_CHECK_SPAWN = "entity.check_spawn";
	String ENTITY_SPAWNED = "entity.spawned";

	String BLOCK_MISSING_MAPPINGS = "block.missing_mappings";
	String BLOCK_RIGHT_CLICK = "block.right_click";
	String BLOCK_LEFT_CLICK = "block.left_click";
	String BLOCK_PLACE = "block.place";
	String BLOCK_BREAK = "block.break";
	String BLOCK_MODIFICATION = "block.modification";

	String ITEM_REGISTRY_TOOL_TIERS = "item.registry.tool_tiers";
	String ITEM_REGISTRY_ARMOR_TIERS = "item.registry.armor_tiers";
	String ITEM_MISSING_MAPPINGS = "item.missing_mappings";
	String ITEM_RIGHT_CLICK = "item.right_click";
	String ITEM_RIGHT_CLICK_EMPTY = "item.right_click_empty";
	String ITEM_LEFT_CLICK = "item.left_click";
	String ITEM_ENTITY_INTERACT = "item.entity_interact";
	String ITEM_PICKUP = "item.pickup";
	String ITEM_TOSS = "item.toss";
	String ITEM_CRAFTED = "item.crafted";
	String ITEM_SMELTED = "item.smelted";
	String ITEM_DESTROYED = "item.destroyed";
	String ITEM_FOOD_EATEN = "item.food_eaten";
	String ITEM_TOOLTIP = "item.tooltip";
	String ITEM_MODIFICATION = "item.modification";
	String ITEM_MODEL_PROPERTIES = "item.model_properties";

	String SOUND_REGISTRY = "sound.registry";
}