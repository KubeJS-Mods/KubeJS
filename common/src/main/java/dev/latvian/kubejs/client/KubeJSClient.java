package dev.latvian.kubejs.client;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSCommon;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.net.NetworkEventJS;
import dev.latvian.kubejs.script.BindingsEvent;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.script.ScriptsLoadedEvent;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.Overlay;
import dev.latvian.kubejs.world.ClientWorldJS;
import dev.latvian.kubejs.world.WorldJS;
import me.shedaniel.architectury.hooks.PackRepositoryHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

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
		PackRepository list = Minecraft.getInstance().getResourcePackRepository();
		PackRepositoryHooks.addSource(list, new KubeJSResourcePackFinder());
		ScriptsLoadedEvent.EVENT.register(this::setup);
	}

	@Override
	public void clientBindings(BindingsEvent event)
	{
		event.add("client", new ClientWrapper());
	}

	private void setup()
	{
		new EventJS().post(ScriptType.CLIENT, KubeJSEvents.CLIENT_INIT);
	}

	@Override
	public void handleDataToClientPacket(String channel, @Nullable CompoundTag data)
	{
		new NetworkEventJS(Minecraft.getInstance().player, channel, MapJS.of(data)).post(KubeJSEvents.PLAYER_DATA_FROM_SERVER, channel);
	}

	@Override
	@Nullable
	public Player getClientPlayer()
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