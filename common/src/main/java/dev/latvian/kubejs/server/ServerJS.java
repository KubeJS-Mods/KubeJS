package dev.latvian.kubejs.server;

import dev.latvian.kubejs.net.SendDataFromServerMessage;
import dev.latvian.kubejs.player.AdvancementJS;
import dev.latvian.kubejs.player.EntityArrayList;
import dev.latvian.kubejs.player.FakeServerPlayerDataJS;
import dev.latvian.kubejs.player.ServerPlayerDataJS;
import dev.latvian.kubejs.player.ServerPlayerJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.AttachedData;
import dev.latvian.kubejs.util.MessageSender;
import dev.latvian.kubejs.util.WithAttachedData;
import dev.latvian.kubejs.world.AttachWorldDataEvent;
import dev.latvian.kubejs.world.ServerWorldJS;
import dev.latvian.kubejs.world.WorldJS;
import dev.latvian.mods.rhino.mod.wrapper.UUIDWrapper;
import net.minecraft.Util;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class ServerJS implements MessageSender, WithAttachedData {
	public static ServerJS instance;

	private MinecraftServer minecraftServer;
	public final transient ServerScriptManager serverScriptManager;
	public final transient List<ScheduledEvent> scheduledEvents;
	public final transient List<ScheduledEvent> scheduledTickEvents;
	public final transient Map<String, ServerWorldJS> levelMap;
	public final transient Map<UUID, ServerPlayerDataJS> playerMap;
	public final transient Map<UUID, FakeServerPlayerDataJS> fakePlayerMap;
	public final transient List<ServerWorldJS> worlds;
	public final CompoundTag persistentData;

	public ServerWorldJS overworld;
	private AttachedData data;

	public ServerJS(MinecraftServer ms, ServerScriptManager m) {
		minecraftServer = ms;
		serverScriptManager = m;
		scheduledEvents = new LinkedList<>();
		scheduledTickEvents = new LinkedList<>();
		levelMap = new HashMap<>();
		playerMap = new HashMap<>();
		fakePlayerMap = new HashMap<>();
		worlds = new ArrayList<>();
		persistentData = new CompoundTag();
	}

	public void release() {
		minecraftServer = null;
		scheduledEvents.clear();
		scheduledTickEvents.clear();
		playerMap.clear();
		fakePlayerMap.clear();
		overworld = null;
		levelMap.clear();
		worlds.clear();
		data = null;
	}

	public void updateWorldList() {
		worlds.clear();
		worlds.addAll(levelMap.values());
	}

	@Override
	public AttachedData getData() {
		if (data == null) {
			data = new AttachedData(this);
		}

		return data;
	}

	public List<ServerWorldJS> getWorlds() {
		return worlds;
	}

	public ServerWorldJS getOverworld() {
		return overworld;
	}

	public MinecraftServer getMinecraftServer() {
		return minecraftServer;
	}

	public boolean isRunning() {
		return getMinecraftServer().isRunning();
	}

	public boolean getHardcore() {
		return getMinecraftServer().isHardcore();
	}

	public boolean isSinglePlayer() {
		return getMinecraftServer().isSingleplayer();
	}

	public boolean isDedicated() {
		return getMinecraftServer().isDedicatedServer();
	}

	public String getMotd() {
		return getMinecraftServer().getMotd();
	}

	public void setMotd(Component text) {
		getMinecraftServer().setMotd(text.getString());
	}

	public void stop() {
		getMinecraftServer().close();
	}

	@Override
	public Text getName() {
		return Text.of(getMinecraftServer().name());
	}

	@Override
	public Text getDisplayName() {
		return Text.of(getMinecraftServer().createCommandSourceStack().getDisplayName());
	}

	@Override
	public void tell(Component message) {
		getMinecraftServer().sendMessage(message, Util.NIL_UUID);

		for (ServerPlayer player : getMinecraftServer().getPlayerList().getPlayers()) {
			player.sendMessage(message, Util.NIL_UUID);
		}
	}

	@Override
	public void setStatusMessage(Component message) {
		for (ServerPlayer player : getMinecraftServer().getPlayerList().getPlayers()) {
			player.displayClientMessage(message, true);
		}
	}

	@Override
	public int runCommand(String command) {
		return getMinecraftServer().getCommands().performCommand(getMinecraftServer().createCommandSourceStack(), command);
	}

	@Override
	public int runCommandSilent(String command) {
		return getMinecraftServer().getCommands().performCommand(getMinecraftServer().createCommandSourceStack().withSuppressedOutput(), command);
	}

	public WorldJS getLevel(String dimension) {
		ServerWorldJS world = levelMap.get(dimension);

		if (world == null) {
			world = new ServerWorldJS(this, getMinecraftServer().getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(dimension))));
			levelMap.put(dimension, world);
			updateWorldList();
			new AttachWorldDataEvent(world).invoke();
		}

		return world;
	}

	@Deprecated
	public WorldJS getWorld(String dimension) {
		return getLevel(dimension);
	}

	public WorldJS getLevel(Level minecraftLevel) {
		ServerWorldJS world = levelMap.get(minecraftLevel.dimension().location().toString());

		if (world == null) {
			world = new ServerWorldJS(this, (ServerLevel) minecraftLevel);
			levelMap.put(minecraftLevel.dimension().location().toString(), world);
			updateWorldList();
			new AttachWorldDataEvent(world).invoke();
		}

		return world;
	}

	@Deprecated
	public WorldJS getWorld(Level minecraftLevel) {
		return getLevel(minecraftLevel);
	}

	@Nullable
	public ServerPlayerJS getPlayer(UUID uuid) {
		ServerPlayerDataJS p = playerMap.get(uuid);

		if (p == null) {
			return null;
		}

		return p.getPlayer();
	}

	@Nullable
	public ServerPlayerJS getPlayer(String name) {
		name = name.trim().toLowerCase();

		if (name.isEmpty()) {
			return null;
		}

		UUID uuid = UUIDWrapper.fromString(name);

		if (uuid != null) {
			return getPlayer(uuid);
		}

		for (ServerPlayerDataJS p : playerMap.values()) {
			if (p.getName().equalsIgnoreCase(name)) {
				return p.getPlayer();
			}
		}

		for (ServerPlayerDataJS p : playerMap.values()) {
			if (p.getName().toLowerCase().contains(name)) {
				return p.getPlayer();
			}
		}

		return null;
	}

	@Nullable
	public ServerPlayerJS getPlayer(Player minecraftPlayer) {
		return getPlayer(minecraftPlayer.getUUID());
	}

	public EntityArrayList getPlayers() {
		return new EntityArrayList(overworld, getMinecraftServer().getPlayerList().getPlayers());
	}

	public EntityArrayList getEntities() {
		EntityArrayList list = new EntityArrayList(overworld, 10);

		for (ServerWorldJS world : worlds) {
			list.addAll(world.getEntities());
		}

		return list;
	}

	public EntityArrayList getEntities(String filter) {
		EntityArrayList list = new EntityArrayList(overworld, 10);

		for (ServerWorldJS world : worlds) {
			list.addAll(world.getEntities(filter));
		}

		return list;
	}

	public ScheduledEvent schedule(long timer, @Nullable Object data, IScheduledEventCallback event) {
		ScheduledEvent e = new ScheduledEvent(this, false, timer, System.currentTimeMillis() + timer, data, event);
		scheduledEvents.add(e);
		return e;
	}

	public ScheduledEvent schedule(long timer, IScheduledEventCallback event) {
		return schedule(timer, null, event);
	}

	public ScheduledEvent scheduleInTicks(long ticks, @Nullable Object data, IScheduledEventCallback event) {
		ScheduledEvent e = new ScheduledEvent(this, true, ticks, overworld.getTime() + ticks, data, event);
		scheduledTickEvents.add(e);
		return e;
	}

	public ScheduledEvent scheduleInTicks(long ticks, IScheduledEventCallback event) {
		return scheduleInTicks(ticks, null, event);
	}

	@Override
	public String toString() {
		return "Server";
	}

	@Nullable
	public AdvancementJS getAdvancement(ResourceLocation id) {
		Advancement a = getMinecraftServer().getAdvancements().getAdvancement(id);
		return a == null ? null : new AdvancementJS(a);
	}

	public void sendDataToAll(String channel, @Nullable CompoundTag data) {
		new SendDataFromServerMessage(channel, data).sendToAll(getMinecraftServer());
	}
}
