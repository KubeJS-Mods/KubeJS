package com.latmod.mods.kubejs.player;

import com.latmod.mods.kubejs.KubeJS;
import com.latmod.mods.kubejs.KubeJSEvents;
import com.latmod.mods.kubejs.entity.EntityJS;
import com.latmod.mods.kubejs.events.EventsJS;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = KubeJS.MOD_ID)
public class KubeJSPlayerEventHandler
{
	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
	{
		if (!KubeJS.ERRORS.isEmpty() && event.player instanceof EntityPlayerMP && event.player.canUseCommand(1, "kubejs.errors"))
		{
			for (ITextComponent component : KubeJS.ERRORS)
			{
				event.player.sendMessage(component);
			}
		}

		if (event.player instanceof EntityPlayerMP)
		{
			PlayerJS p = new PlayerJS(KubeJS.server.getWorld(event.player.world.provider.getDimension()), (EntityPlayerMP) event.player);
			KubeJS.server.playerMap.put(p.getID(), p);
			KubeJS.server.players.clear();
			KubeJS.server.players.addAll(KubeJS.server.playerMap.values());
			KubeJS.server.players.sort(EntityJS.COMPARATOR);
			EventsJS.INSTANCE.post(KubeJSEvents.PLAYER_LOGGED_IN, new PlayerEventJS(p));
		}
	}

	@SubscribeEvent
	public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event)
	{
		EventsJS.INSTANCE.post(KubeJSEvents.PLAYER_LOGGED_OUT, new PlayerEventJS(event.player));
		KubeJS.server.playerMap.remove(event.player.getUniqueID());
		KubeJS.server.players.clear();
		KubeJS.server.players.addAll(KubeJS.server.playerMap.values());
		KubeJS.server.players.sort(EntityJS.COMPARATOR);
	}

	@SubscribeEvent
	public static void onPlayerChat(ServerChatEvent event)
	{
		PlayerChatEventJS e = new PlayerChatEventJS(event);
		boolean c = EventsJS.INSTANCE.post(KubeJSEvents.PLAYER_CHAT, e);

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