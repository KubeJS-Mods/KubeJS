package dev.latvian.kubejs;

import dev.latvian.kubejs.block.BlockRegistryEventJS;
import dev.latvian.kubejs.crafting.handlers.CraftingTableRecipeEventJS;
import dev.latvian.kubejs.crafting.handlers.FurnaceRecipeEventJS;
import dev.latvian.kubejs.entity.LivingEntityDeathEventJS;
import dev.latvian.kubejs.item.ItemRegistryEventJS;
import dev.latvian.kubejs.player.PlayerChatEventJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import dev.latvian.kubejs.server.CommandRegistryEventJS;
import dev.latvian.kubejs.server.ServerEventJS;
import dev.latvian.kubejs.world.WorldEventJS;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = KubeJS.MOD_ID)
public class KubeJSEvents
{
	public static final String POSTINIT = "postinit";
	public static final String SERVER_LOAD = "server.load";
	public static final String SERVER_UNLOAD = "server.unload";
	public static final String SERVER_TICK = "server.tick";
	public static final String WORLD_LOAD = "world.load";
	public static final String WORLD_UNLOAD = "world.unload";
	public static final String WORLD_TICK = "world.tick";
	public static final String PLAYER_LOGGED_IN = "player.logged_in";
	public static final String PLAYER_LOGGED_OUT = "player.logged_out";
	public static final String PLAYER_TICK = "player.tick";
	public static final String PLAYER_CHAT = "player.chat";
	public static final String ENTITY_DEATH = "entity.death";
	public static final String RECIPES_CRAFTING_TABLE = "recipes.crafting_table";
	public static final String RECIPES_FURNACE = "recipes.furnace";
	public static final String BLOCK_REGISTRY = "block.registry";
	public static final String ITEM_REGISTRY = "item.registry";
	public static final String COMMAND_REGISTRY = "command.registry";

	@SubscribeEvent
	public static void registerWJSEvents(KubeJSEventRegistryEvent event)
	{
		event.register(POSTINIT, PostInitEventJS.class);
		event.register(SERVER_LOAD, ServerEventJS.class);
		event.register(SERVER_UNLOAD, ServerEventJS.class);
		event.register(SERVER_TICK, ServerEventJS.class);
		event.register(WORLD_LOAD, WorldEventJS.class);
		event.register(WORLD_UNLOAD, WorldEventJS.class);
		event.register(WORLD_TICK, WorldEventJS.class);
		event.register(PLAYER_LOGGED_IN, PlayerEventJS.class);
		event.register(PLAYER_LOGGED_OUT, PlayerEventJS.class);
		event.register(PLAYER_TICK, PlayerEventJS.class);
		event.register(PLAYER_CHAT, PlayerChatEventJS.class);
		event.register(ENTITY_DEATH, LivingEntityDeathEventJS.class);
		event.register(RECIPES_CRAFTING_TABLE, CraftingTableRecipeEventJS.class);
		event.register(RECIPES_FURNACE, FurnaceRecipeEventJS.class);
		event.register(BLOCK_REGISTRY, BlockRegistryEventJS.class);
		event.register(ITEM_REGISTRY, ItemRegistryEventJS.class);
		event.register(COMMAND_REGISTRY, CommandRegistryEventJS.class);
	}
}