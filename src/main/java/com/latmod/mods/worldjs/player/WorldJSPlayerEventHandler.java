package com.latmod.mods.worldjs.player;

import com.latmod.mods.worldjs.WorldJSMod;
import com.latmod.mods.worldjs.entity.EntityJS;
import com.latmod.mods.worldjs.events.EventsJS;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = WorldJSMod.MOD_ID)
public class WorldJSPlayerEventHandler
{
	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
	{
		if (!WorldJSMod.ERRORS.isEmpty() && event.player instanceof EntityPlayerMP && event.player.canUseCommand(1, "worldjs.errors"))
		{
			for (ITextComponent component : WorldJSMod.ERRORS)
			{
				event.player.sendMessage(component);
			}
		}

		if (event.player instanceof EntityPlayerMP)
		{
			PlayerJS p = new PlayerJS(WorldJSMod.server.getWorld(event.player.world.provider.getDimension()), (EntityPlayerMP) event.player);
			WorldJSMod.server.playerMap.put(p.getID(), p);
			WorldJSMod.server.players.clear();
			WorldJSMod.server.players.addAll(WorldJSMod.server.playerMap.values());
			WorldJSMod.server.players.sort(EntityJS.COMPARATOR);
			EventsJS.INSTANCE.post("player.logged_in", new PlayerEventJS(p));
		}
	}

	@SubscribeEvent
	public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event)
	{
		EventsJS.INSTANCE.post("player.logged_out", new PlayerEventJS(event.player));
		WorldJSMod.server.playerMap.remove(event.player.getUniqueID());
		WorldJSMod.server.players.clear();
		WorldJSMod.server.players.addAll(WorldJSMod.server.playerMap.values());
		WorldJSMod.server.players.sort(EntityJS.COMPARATOR);
	}

	@SubscribeEvent
	public static void onPlayerChat(ServerChatEvent event)
	{
		PlayerChatEventJS e = new PlayerChatEventJS(event);
		boolean c = EventsJS.INSTANCE.post("player.chat", e);

		if (e.component != null)
		{
			event.setComponent(e.component.component());
		}

		if (c)
		{
			event.setCanceled(true);
		}
	}
}