package dev.latvian.kubejs.client;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.event.EventsJS;
import dev.latvian.kubejs.item.BlockItemJS;
import dev.latvian.kubejs.item.ItemJS;
import dev.latvian.kubejs.script.BindingsEvent;
import dev.latvian.kubejs.world.ClientWorldJS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = KubeJS.MOD_ID, value = Side.CLIENT)
public class KubeJSClientEventHandler
{
	@SubscribeEvent
	public static void onBindings(BindingsEvent event)
	{
		event.add("client", new ClientWrapper());
	}

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event)
	{
		for (Item item : Item.REGISTRY)
		{
			if (item instanceof ItemJS)
			{
				ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(((ItemJS) item).properties.model));
			}
			else if (item instanceof BlockItemJS)
			{
				ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(((BlockItemJS) item).properties.model));
			}
		}
	}

	@SubscribeEvent
	public static void debugInfo(RenderGameOverlayEvent.Text event)
	{
		if (Minecraft.getMinecraft().player != null)
		{
			ClientWorldJS.get();
			EventsJS.post(KubeJSEvents.CLIENT_DEBUG_INFO, new DebugInfoEventJS(event));
		}
	}

	@SubscribeEvent
	public static void clientTick(TickEvent.ClientTickEvent event)
	{
		if (Minecraft.getMinecraft().player != null)
		{
			EventsJS.post(KubeJSEvents.CLIENT_TICK, new ClientTickEventJS(ClientWorldJS.get().clientPlayerData.getPlayer()));
		}
	}

	@SubscribeEvent
	public static void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
	{
		ClientWorldJS.invalidate();
	}
}