package dev.latvian.kubejs.player;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.server.ServerJS;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;

/**
 * @author LatvianModder
 */
public class KubeJSPlayerEventHandler
{
	public void init()
	{
		MinecraftForge.EVENT_BUS.addListener(this::loggedIn);
		MinecraftForge.EVENT_BUS.addListener(this::loggedOut);
		MinecraftForge.EVENT_BUS.addListener(this::cloned);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::tick);
		MinecraftForge.EVENT_BUS.addListener(this::chat);
		MinecraftForge.EVENT_BUS.addListener(this::advancement);
		MinecraftForge.EVENT_BUS.addListener(this::inventoryOpened);
		MinecraftForge.EVENT_BUS.addListener(this::inventoryClosed);
	}

	public void loggedIn(PlayerEvent.PlayerLoggedInEvent event)
	{
		if (ServerJS.instance != null && event.getPlayer() instanceof ServerPlayer)
		{
			ServerPlayerDataJS p = new ServerPlayerDataJS(ServerJS.instance, event.getPlayer().getUniqueID(), event.getPlayer().getGameProfile().getName(), KubeJS.nextClientHasClientMod);
			KubeJS.nextClientHasClientMod = false;
			p.getServer().playerMap.put(p.getId(), p);
			MinecraftForge.EVENT_BUS.post(new AttachPlayerDataEvent(p));
			new SimplePlayerEventJS(event.getPlayer()).post(KubeJSEvents.PLAYER_LOGGED_IN);
			event.getPlayer().container.addListener(new InventoryListener((ServerPlayer) event.getPlayer()));
		}
	}

	public void loggedOut(PlayerEvent.PlayerLoggedOutEvent event)
	{
		if (ServerJS.instance == null || !ServerJS.instance.playerMap.containsKey(event.getPlayer().getUniqueID()))
		{
			return;
		}

		new SimplePlayerEventJS(event.getPlayer()).post(KubeJSEvents.PLAYER_LOGGED_OUT);
		ServerJS.instance.playerMap.remove(event.getPlayer().getUniqueID());
	}

	public void cloned(net.minecraftforge.event.entity.player.PlayerEvent.Clone event)
	{
		if (event.getPlayer() instanceof ServerPlayer)
		{
			event.getPlayer().container.addListener(new InventoryListener((ServerPlayer) event.getPlayer()));
		}
	}

	public void tick(TickEvent.PlayerTickEvent event)
	{
		if (ServerJS.instance != null && event.phase == TickEvent.Phase.END)
		{
			new SimplePlayerEventJS(event.player).post(KubeJSEvents.PLAYER_TICK);
		}
	}

	public void chat(ServerChatEvent event)
	{
		if (new PlayerChatEventJS(event).post(KubeJSEvents.PLAYER_CHAT))
		{
			event.setCanceled(true);
		}
	}

	public void advancement(AdvancementEvent event)
	{
		new PlayerAdvancementEventJS(event).post(KubeJSEvents.PLAYER_ADVANCEMENT);
	}

	public void inventoryOpened(PlayerContainerEvent.Open event)
	{
		if (event.getPlayer() instanceof ServerPlayer && !(event.getContainer() instanceof InventoryMenu))
		{
			event.getContainer().addListener(new InventoryListener((ServerPlayer) event.getPlayer()));
		}

		new InventoryEventJS(event).post(KubeJSEvents.PLAYER_INVENTORY_OPENED);

		if (event.getContainer() instanceof ChestMenu)
		{
			new ChestEventJS(event).post(KubeJSEvents.PLAYER_CHEST_OPENED);
		}
	}

	public void inventoryClosed(PlayerContainerEvent.Close event)
	{
		new InventoryEventJS(event).post(KubeJSEvents.PLAYER_INVENTORY_CLOSED);

		if (event.getContainer() instanceof ChestMenu)
		{
			new ChestEventJS(event).post(KubeJSEvents.PLAYER_CHEST_CLOSED);
		}
	}
}