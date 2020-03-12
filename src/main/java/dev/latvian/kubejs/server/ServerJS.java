package dev.latvian.kubejs.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.KubeJSCore;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.MinecraftClass;
import dev.latvian.kubejs.block.BlockJS;
import dev.latvian.kubejs.net.KubeJSNet;
import dev.latvian.kubejs.net.MessageSendDataFromServer;
import dev.latvian.kubejs.player.AdvancementJS;
import dev.latvian.kubejs.player.EntityArrayList;
import dev.latvian.kubejs.player.FakeServerPlayerDataJS;
import dev.latvian.kubejs.player.PlayerDataJS;
import dev.latvian.kubejs.player.PlayerJS;
import dev.latvian.kubejs.player.ServerPlayerDataJS;
import dev.latvian.kubejs.recipe.RecipeEventJS;
import dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent;
import dev.latvian.kubejs.script.ScriptFile;
import dev.latvian.kubejs.script.ScriptFileInfo;
import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.kubejs.script.ScriptPack;
import dev.latvian.kubejs.script.ScriptPackInfo;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.script.data.DataPackEventJS;
import dev.latvian.kubejs.script.data.VirtualKubeJSDataPack;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.AttachedData;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.MessageSender;
import dev.latvian.kubejs.util.UUIDUtilsJS;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.util.WithAttachedData;
import dev.latvian.kubejs.world.AttachWorldDataEvent;
import dev.latvian.kubejs.world.ServerWorldJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.advancements.Advancement;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.resources.FallbackResourceManager;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author LatvianModder
 */
public class ServerJS implements MessageSender, WithAttachedData
{
	public static ServerJS instance;

	@MinecraftClass
	public final MinecraftServer minecraftServer;
	public final ScriptManager scriptManager;
	public final List<ScheduledEvent> scheduledEvents;
	public final List<ScheduledEvent> scheduledTickEvents;
	public final Map<DimensionType, ServerWorldJS> worldMap;
	public final Map<UUID, ServerPlayerDataJS> playerMap;
	public final Map<UUID, FakeServerPlayerDataJS> fakePlayerMap;
	public final List<ServerWorldJS> worlds;

	public ServerWorldJS overworld;
	private AttachedData data;
	private final VirtualKubeJSDataPack virtualDataPackFirst;
	private final VirtualKubeJSDataPack virtualDataPackLast;
	public boolean dataPackOutput;
	public boolean logAddedRecipes;
	public boolean logRemovedRecipes;

	public ServerJS(MinecraftServer ms)
	{
		minecraftServer = ms;
		scriptManager = new ScriptManager(ScriptType.SERVER);
		scheduledEvents = new LinkedList<>();
		scheduledTickEvents = new LinkedList<>();
		worldMap = new HashMap<>();
		playerMap = new HashMap<>();
		fakePlayerMap = new HashMap<>();
		worlds = new ArrayList<>();
		virtualDataPackFirst = new VirtualKubeJSDataPack(true);
		virtualDataPackLast = new VirtualKubeJSDataPack(false);
		dataPackOutput = false;
		logAddedRecipes = false;
		logRemovedRecipes = false;
	}

	public void updateWorldList()
	{
		worlds.clear();
		worlds.addAll(worldMap.values());
	}

	@Override
	public AttachedData getData()
	{
		if (data == null)
		{
			data = new AttachedData(this);
		}

		return data;
	}

	public List<ServerWorldJS> getWorlds()
	{
		return worlds;
	}

	public ServerWorldJS getOverworld()
	{
		return overworld;
	}

	public boolean isRunning()
	{
		return minecraftServer.isServerRunning();
	}

	public boolean getHardcore()
	{
		return minecraftServer.isHardcore();
	}

	public void setHardcore(boolean hardcore)
	{
		overworld.minecraftWorld.getWorldInfo().setHardcore(hardcore);
	}

	public boolean isSinglePlayer()
	{
		return minecraftServer.isSinglePlayer();
	}

	public boolean isDedicated()
	{
		return minecraftServer.isDedicatedServer();
	}

	public String getMotd()
	{
		return minecraftServer.getMOTD();
	}

	public void setMotd(Object text)
	{
		minecraftServer.setMOTD(Text.of(text).component().getFormattedText());
	}

	public void stop()
	{
		minecraftServer.close();
	}

	@Override
	public Text getName()
	{
		return Text.of(minecraftServer.getName());
	}

	@Override
	public Text getDisplayName()
	{
		return Text.of(minecraftServer.getCommandSource().getDisplayName());
	}

	@Override
	public void tell(Object message)
	{
		ITextComponent component = Text.of(message).component();
		minecraftServer.sendMessage(component);

		for (ServerPlayerEntity player : minecraftServer.getPlayerList().getPlayers())
		{
			player.sendMessage(component);
		}
	}

	@Override
	public void setStatusMessage(Object message)
	{
		ITextComponent component = Text.of(message).component();

		for (ServerPlayerEntity player : minecraftServer.getPlayerList().getPlayers())
		{
			player.sendStatusMessage(component, true);
		}
	}

	@Override
	public int runCommand(String command)
	{
		return minecraftServer.getCommandManager().handleCommand(minecraftServer.getCommandSource(), command);
	}

	public WorldJS getWorld(DimensionType dimension)
	{
		if (dimension == DimensionType.OVERWORLD)
		{
			return overworld;
		}

		ServerWorldJS world = worldMap.get(dimension);

		if (world == null)
		{
			world = new ServerWorldJS(this, minecraftServer.getWorld(dimension));
			worldMap.put(dimension, world);
			updateWorldList();
			MinecraftForge.EVENT_BUS.post(new AttachWorldDataEvent(world));
		}

		return world;
	}

	public WorldJS getWorld(IWorld minecraftWorld)
	{
		return getWorld(minecraftWorld.getDimension().getType());
	}

	@Nullable
	public PlayerJS getPlayer(UUID uuid)
	{
		ServerPlayerDataJS p = playerMap.get(uuid);

		if (p == null)
		{
			return null;
		}

		return p.getPlayer();
	}

	@Nullable
	public PlayerJS getPlayer(String name)
	{
		name = name.trim().toLowerCase();

		if (name.isEmpty())
		{
			return null;
		}

		UUID uuid = UUIDUtilsJS.fromString(name);

		if (uuid != null)
		{
			return getPlayer(uuid);
		}

		for (PlayerDataJS p : playerMap.values())
		{
			if (p.getName().equalsIgnoreCase(name))
			{
				return p.getPlayer();
			}
		}

		for (PlayerDataJS p : playerMap.values())
		{
			if (p.getName().toLowerCase().contains(name))
			{
				return p.getPlayer();
			}
		}

		return null;
	}

	@Nullable
	public PlayerJS getPlayer(PlayerEntity minecraftPlayer)
	{
		return getPlayer(minecraftPlayer.getUniqueID());
	}

	public EntityArrayList getPlayers()
	{
		return new EntityArrayList(overworld, minecraftServer.getPlayerList().getPlayers());
	}

	public EntityArrayList getEntities()
	{
		EntityArrayList list = new EntityArrayList(overworld, 10);

		for (ServerWorldJS world : worlds)
		{
			list.addAll(world.getEntities());
		}

		return list;
	}

	public EntityArrayList getEntities(String filter)
	{
		EntityArrayList list = new EntityArrayList(overworld, 10);

		for (ServerWorldJS world : worlds)
		{
			list.addAll(world.getEntities(filter));
		}

		return list;
	}

	public ScheduledEvent schedule(long timer, @Nullable Object data, IScheduledEventCallback event)
	{
		ScheduledEvent e = new ScheduledEvent(this, false, timer, System.currentTimeMillis() + timer, data, event);
		scheduledEvents.add(e);
		return e;
	}

	public ScheduledEvent schedule(long timer, IScheduledEventCallback event)
	{
		return schedule(timer, null, event);
	}

	public ScheduledEvent scheduleInTicks(long ticks, @Nullable Object data, IScheduledEventCallback event)
	{
		ScheduledEvent e = new ScheduledEvent(this, true, ticks, overworld.getTime() + ticks, data, event);
		scheduledEvents.add(e);
		return e;
	}

	public ScheduledEvent scheduleInTicks(long ticks, IScheduledEventCallback event)
	{
		return scheduleInTicks(ticks, null, event);
	}

	@Override
	public String toString()
	{
		return "Server";
	}

	@Nullable
	public AdvancementJS getAdvancement(Object id)
	{
		Advancement a = minecraftServer.getAdvancementManager().getAdvancement(UtilsJS.getID(id));
		return a == null ? null : new AdvancementJS(a);
	}

	public void sendDataToAll(String channel, @Nullable Object data)
	{
		KubeJSNet.MAIN.send(PacketDistributor.ALL.noArg(), new MessageSendDataFromServer(channel, MapJS.nbt(data)));
	}

	public void reloadScripts(SimpleReloadableResourceManager resourceManager)
	{
		scriptManager.unload();

		Set<String> namespaces = new LinkedHashSet<>(resourceManager.getResourceNamespaces());

		for (String namespace : namespaces)
		{
			try (InputStreamReader reader = new InputStreamReader(resourceManager.getResource(new ResourceLocation(namespace, "kubejs/scripts.json")).getInputStream()))
			{
				ScriptPack pack = new ScriptPack(scriptManager, new ScriptPackInfo(namespace, reader, "kubejs/"));

				for (ScriptFileInfo fileInfo : pack.info.scripts)
				{
					pack.scripts.add(new ScriptFile(pack, fileInfo, info -> new InputStreamReader(resourceManager.getResource(info.location).getInputStream())));
				}

				scriptManager.packs.put(pack.info.namespace, pack);
			}
			catch (Exception ex)
			{
			}
		}

		//Loading is required in prepare stage to allow virtual data pack overrides
		virtualDataPackFirst.resetData();
		ScriptType.SERVER.console.setLineNumber(true);
		scriptManager.load();

		new DataPackEventJS(virtualDataPackFirst).post(ScriptType.SERVER, KubeJSEvents.SERVER_DATAPACK_FIRST);
		new DataPackEventJS(virtualDataPackLast).post(ScriptType.SERVER, KubeJSEvents.SERVER_DATAPACK_LAST);

		RecipeEventJS recipeEvent = new RecipeEventJS();
		MinecraftForge.EVENT_BUS.post(new RegisterRecipeHandlersEvent(recipeEvent));
		recipeEvent.loadRecipes(resourceManager);
		recipeEvent.post(ScriptType.SERVER, KubeJSEvents.SERVER_DATAPACK_RECIPES);
		recipeEvent.addDataToPack(virtualDataPackFirst);

		for (BlockJS block : BlockJS.KUBEJS_BLOCKS.values())
		{
			JsonObject json = new JsonObject();
			json.addProperty("type", "minecraft:block");
			JsonArray pools = new JsonArray();
			JsonObject pool = new JsonObject();
			pool.addProperty("rolls", 1);
			JsonArray entries = new JsonArray();
			JsonObject entry = new JsonObject();
			entry.addProperty("type", "minecraft:item");
			entry.addProperty("name", block.properties.id.getNamespace() + ":" + block.properties.id.getPath());
			entries.add(entry);
			pool.add("entries", entries);
			JsonArray conditions = new JsonArray();
			JsonObject condition = new JsonObject();
			condition.addProperty("condition", "minecraft:survives_explosion");
			conditions.add(condition);
			pool.add("conditions", conditions);
			pools.add(pool);
			json.add("pools", pools);
			virtualDataPackFirst.addData(new ResourceLocation(block.properties.id.getNamespace(), "loot_tables/blocks/" + block.properties.id.getPath() + ".json"), json.toString());
		}

		resourceManager.addResourcePack(virtualDataPackFirst);
		resourceManager.addResourcePack(virtualDataPackLast);

		Map<String, FallbackResourceManager> namespaceResourceManagers = KubeJSCore.getNamespaceResourceManagers(resourceManager);

		for (FallbackResourceManager manager : namespaceResourceManagers.values())
		{
			if (manager.resourcePacks.remove(virtualDataPackLast))
			{
				manager.resourcePacks.add(0, virtualDataPackLast);
			}
		}

		ScriptType.SERVER.console.setLineNumber(false);
		ScriptType.SERVER.console.info("Scripts loaded");

		for (int i = 0; i < scriptManager.errors.size(); i++)
		{
			minecraftServer.getPlayerList().sendMessage(new StringTextComponent("#" + (i + 1) + ": ").applyTextStyle(TextFormatting.DARK_RED).appendSibling(new StringTextComponent(scriptManager.errors.get(i)).applyTextStyle(TextFormatting.RED)));
		}
	}

	public IFutureReloadListener createReloadListener()
	{
		return (stage, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor) -> {
			if (!(resourceManager instanceof SimpleReloadableResourceManager))
			{
				throw new RuntimeException("Resource manager is not SimpleReloadableResourceManager, KubeJS will not work! Unsupported resource manager class: " + resourceManager.getClass());
			}

			reloadScripts((SimpleReloadableResourceManager) resourceManager);
			return CompletableFuture.supplyAsync(Object::new, backgroundExecutor).thenCompose(stage::markCompleteAwaitingOthers).thenAcceptAsync(o -> {}, gameExecutor);
		};
	}
}