package com.latmod.mods.kubejs;

import com.latmod.mods.kubejs.crafting.RecipeEventJS;
import com.latmod.mods.kubejs.player.PlayerChatEventJS;
import com.latmod.mods.kubejs.player.PlayerEventJS;
import com.latmod.mods.kubejs.world.ServerEventJS;
import com.latmod.mods.kubejs.world.WorldEventJS;
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
	public static final String WORLD_LOAD = "world.load";
	public static final String WORLD_UNLOAD = "world.unload";
	public static final String PLAYER_LOGGED_IN = "player.logged_in";
	public static final String PLAYER_LOGGED_OUT = "player.logged_out";
	public static final String PLAYER_CHAT = "player.chat";
	public static final String RECIPES = "recipes";

	@SubscribeEvent
	public static void registerWJSEvents(KubeJSEventRegistryEvent event)
	{
		event.register(POSTINIT, PostInitEventJS.class);
		event.register(SERVER_LOAD, ServerEventJS.class);
		event.register(SERVER_UNLOAD, ServerEventJS.class);
		event.register(WORLD_LOAD, WorldEventJS.class);
		event.register(WORLD_UNLOAD, WorldEventJS.class);
		event.register(PLAYER_LOGGED_IN, PlayerEventJS.class);
		event.register(PLAYER_LOGGED_OUT, PlayerEventJS.class);
		event.register(PLAYER_CHAT, PlayerChatEventJS.class);
		event.register(RECIPES, RecipeEventJS.class);
	}
}