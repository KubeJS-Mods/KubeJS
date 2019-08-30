package dev.latvian.kubejs.player;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.entity.LivingEntityDeathEventJS;
import dev.latvian.kubejs.event.EventsJS;
import dev.latvian.kubejs.script.ScriptFile;
import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.kubejs.server.ServerJS;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = KubeJS.MOD_ID)
public class KubeJSPlayerEventHandler
{
	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
	{
		if (event.player instanceof EntityPlayerMP && (((EntityPlayerMP) event.player).server.isSinglePlayer() || event.player.canUseCommand(1, "kubejs.errors")))
		{
			for (ScriptFile file : ScriptManager.instance.scripts.values())
			{
				ITextComponent component = file.getErrorTextComponent();

				if (component != null)
				{
					event.player.sendMessage(component);
				}
			}
		}

		if (ServerJS.instance != null && event.player instanceof EntityPlayerMP)
		{
			PlayerDataJS p = new PlayerDataJS(ServerJS.instance, (EntityPlayerMP) event.player);
			p.server.playerMap.put(p.uuid, p);
			MinecraftForge.EVENT_BUS.post(new PlayerDataCreatedEvent(p));
			EventsJS.post(KubeJSEvents.PLAYER_LOGGED_IN, new PlayerEventJS(event.player));
		}
	}

	@SubscribeEvent
	public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event)
	{
		if (ServerJS.instance == null || !ServerJS.instance.playerMap.containsKey(event.player.getUniqueID()))
		{
			return;
		}

		EventsJS.post(KubeJSEvents.PLAYER_LOGGED_OUT, new PlayerEventJS(event.player));
		ServerJS.instance.playerMap.remove(event.player.getUniqueID());
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onPlayerTick(TickEvent.PlayerTickEvent event)
	{
		if (ServerJS.instance != null && event.phase == TickEvent.Phase.END && event.player instanceof EntityPlayerMP)
		{
			EventsJS.post(KubeJSEvents.PLAYER_TICK, new PlayerEventJS(event.player));
		}
	}

	@SubscribeEvent
	public static void onPlayerChat(ServerChatEvent event)
	{
		PlayerChatEventJS e = new PlayerChatEventJS(event);
		boolean c = EventsJS.post(KubeJSEvents.PLAYER_CHAT, e);

		if (e.component != null)
		{
			event.setComponent(e.component.component());
		}

		if (c)
		{
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onLivingDeath(LivingDeathEvent event)
	{
		if (!event.getEntity().world.isRemote && EventsJS.post(KubeJSEvents.ENTITY_DEATH, new LivingEntityDeathEventJS(event)))
		{
			event.setCanceled(true);
		}
	}
}