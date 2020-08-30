package dev.latvian.kubejs.client;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSCommon;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.net.NetworkEventJS;
import dev.latvian.kubejs.script.BindingsEvent;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.Overlay;
import dev.latvian.kubejs.world.ClientWorldJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.resources.ResourcePackList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class KubeJSClient extends KubeJSCommon
{
	public static final Map<String, Overlay> activeOverlays = new LinkedHashMap<>();

	@Override
	public void init()
	{
		KubeJS.clientScriptManager.unload();
		KubeJS.clientScriptManager.loadFromDirectory();
		KubeJS.clientScriptManager.load();

		new KubeJSClientEventHandler().init();
		ResourcePackList list = Minecraft.getInstance().getResourcePackList();
		list.addPackFinder(new KubeJSResourcePackFinder());
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
	}

	@Override
	public void clientBindings(BindingsEvent event)
	{
		event.add("client", new ClientWrapper());
	}

	private void setup(FMLClientSetupEvent event)
	{
		new EventJS().post(ScriptType.CLIENT, KubeJSEvents.CLIENT_INIT);
	}

	@Override
	public void handleDataToClientPacket(String channel, @Nullable CompoundNBT data)
	{
		new NetworkEventJS(Minecraft.getInstance().player, channel, MapJS.of(data)).post(KubeJSEvents.PLAYER_DATA_FROM_SERVER, channel);
	}

	@Override
	@Nullable
	public PlayerEntity getClientPlayer()
	{
		return Minecraft.getInstance().player;
	}

	@Override
	public void openOverlay(Overlay o)
	{
		activeOverlays.put(o.id, o);
	}

	@Override
	public void closeOverlay(String id)
	{
		activeOverlays.remove(id);
	}

	@Override
	public WorldJS getClientWorld()
	{
		return ClientWorldJS.instance;
	}
}