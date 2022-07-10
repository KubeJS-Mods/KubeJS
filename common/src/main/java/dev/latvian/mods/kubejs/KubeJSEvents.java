package dev.latvian.mods.kubejs;

/**
 * @author LatvianModder
 */
public interface KubeJSEvents {
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

	String PLAYER_ADVANCEMENT = "player.advancement";

	String ENTITY_DEATH = "entity.death";
	String ENTITY_HURT = "entity.hurt";
	String ENTITY_CHECK_SPAWN = "entity.check_spawn";
	String ENTITY_SPAWNED = "entity.spawned";

	String BLOCK_RIGHT_CLICK = "block.right_click";
	String BLOCK_LEFT_CLICK = "block.left_click";
	String BLOCK_PLACE = "block.place";
	String BLOCK_BREAK = "block.break";
	String BLOCK_MODIFICATION = "block.modification";

	String ITEM_FOOD_EATEN = "item.food_eaten";
	String ITEM_TOOLTIP = "item.tooltip";
	String ITEM_MODIFICATION = "item.modification";
	String ITEM_MODEL_PROPERTIES = "item.model_properties";
}