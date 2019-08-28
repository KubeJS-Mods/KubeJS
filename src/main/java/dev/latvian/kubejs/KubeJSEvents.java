package dev.latvian.kubejs;

import dev.latvian.kubejs.block.BlockRegistryEventJS;
import dev.latvian.kubejs.command.CommandRegistryEventJS;
import dev.latvian.kubejs.crafting.handlers.CraftingTableRecipeEventJS;
import dev.latvian.kubejs.crafting.handlers.FurnaceRecipeEventJS;
import dev.latvian.kubejs.documentation.DocumentationEvent;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.entity.LivingEntityDeathEventJS;
import dev.latvian.kubejs.entity.LivingEntityJS;
import dev.latvian.kubejs.item.ItemRegistryEventJS;
import dev.latvian.kubejs.player.PlayerChatEventJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import dev.latvian.kubejs.player.PlayerJS;
import dev.latvian.kubejs.server.ServerEventJS;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.world.WorldEventJS;
import dev.latvian.kubejs.world.WorldJS;
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
	public static void registerDocumentation(DocumentationEvent event)
	{
		event.registerNative("void", void.class, Void.class);
		event.registerNative("byte", byte.class, Byte.class);
		event.registerNative("short", short.class, Short.class);
		event.registerNative("int", int.class, Integer.class);
		event.registerNative("long", long.class, Long.class);
		event.registerNative("float", float.class, Float.class);
		event.registerNative("double", double.class, Double.class);
		event.registerNative("char", char.class, Character.class);
		event.registerNative("string", String.class, CharSequence.class);
		event.registerNative("object", Object.class);

		event.registerPackage(ServerJS.class.getPackage());
		event.registerPackage(WorldJS.class.getPackage());
		event.registerPackage(EntityJS.class.getPackage());
		event.registerPackage(LivingEntityJS.class.getPackage());
		event.registerPackage(PlayerJS.class.getPackage());

		event.registerEvent(POSTINIT, PostInitEventJS.class);
		event.registerEvent(SERVER_LOAD, ServerEventJS.class);
		event.registerEvent(SERVER_UNLOAD, ServerEventJS.class);
		event.registerEvent(SERVER_TICK, ServerEventJS.class);
		event.registerEvent(WORLD_LOAD, WorldEventJS.class);
		event.registerEvent(WORLD_UNLOAD, WorldEventJS.class);
		event.registerEvent(WORLD_TICK, WorldEventJS.class);
		event.registerEvent(PLAYER_LOGGED_IN, PlayerEventJS.class);
		event.registerEvent(PLAYER_LOGGED_OUT, PlayerEventJS.class);
		event.registerEvent(PLAYER_TICK, PlayerEventJS.class);
		event.registerEvent(PLAYER_CHAT, PlayerChatEventJS.class);
		event.registerEvent(ENTITY_DEATH, LivingEntityDeathEventJS.class);
		event.registerEvent(RECIPES_CRAFTING_TABLE, CraftingTableRecipeEventJS.class);
		event.registerEvent(RECIPES_FURNACE, FurnaceRecipeEventJS.class);
		event.registerEvent(BLOCK_REGISTRY, BlockRegistryEventJS.class);
		event.registerEvent(ITEM_REGISTRY, ItemRegistryEventJS.class);
		event.registerEvent(COMMAND_REGISTRY, CommandRegistryEventJS.class);
	}
}