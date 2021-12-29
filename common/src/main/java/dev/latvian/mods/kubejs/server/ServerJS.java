package dev.latvian.mods.kubejs.server;

import dev.latvian.mods.kubejs.net.SendDataFromServerMessage;
import dev.latvian.mods.kubejs.player.AdvancementJS;
import dev.latvian.mods.kubejs.player.EntityArrayList;
import dev.latvian.mods.kubejs.player.FakeServerPlayerDataJS;
import dev.latvian.mods.kubejs.player.ServerPlayerDataJS;
import dev.latvian.mods.kubejs.player.ServerPlayerJS;
import dev.latvian.mods.kubejs.text.Text;
import dev.latvian.mods.kubejs.util.AttachedData;
import dev.latvian.mods.kubejs.util.MessageSender;
import dev.latvian.mods.kubejs.util.WithAttachedData;
import dev.latvian.mods.kubejs.world.AttachWorldDataEvent;
import dev.latvian.mods.kubejs.world.ServerWorldJS;
import dev.latvian.mods.kubejs.world.WorldJS;
import dev.latvian.mods.rhino.mod.wrapper.UUIDWrapper;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
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
	public final transient List<ServerWorldJS> allLevels;
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
		allLevels = new ArrayList<>();
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
		allLevels.clear();
		data = null;
	}

	public void updateWorldList() {
		allLevels.clear();
		allLevels.addAll(levelMap.values());
	}

	@Override
	public AttachedData getData() {
		if (data == null) {
			data = new AttachedData(this);
		}

		return data;
	}

	@Deprecated(forRemoval = true)
	@ApiStatus.ScheduledForRemoval(inVersion = "4.1")
	public List<ServerWorldJS> getWorlds() {
		return getAllLevels();
	}

	public List<ServerWorldJS> getAllLevels() {
		return allLevels;
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

		for (var player : getMinecraftServer().getPlayerList().getPlayers()) {
			player.sendMessage(message, Util.NIL_UUID);
		}
	}

	@Override
	public void setStatusMessage(Component message) {
		for (var player : getMinecraftServer().getPlayerList().getPlayers()) {
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
		var level = levelMap.get(dimension);

		if (level == null) {
			level = new ServerWorldJS(this, getMinecraftServer().getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(dimension))));
			levelMap.put(dimension, level);
			updateWorldList();
			new AttachWorldDataEvent(level).invoke();
		}

		return level;
	}

	@Deprecated(forRemoval = true)
	@ApiStatus.ScheduledForRemoval(inVersion = "4.1")
	public final WorldJS getWorld(String dimension) {
		return getLevel(dimension);
	}

	public WorldJS getLevel(Level minecraftLevel) {
		var level = levelMap.get(minecraftLevel.dimension().location().toString());

		if (level == null) {
			level = new ServerWorldJS(this, (ServerLevel) minecraftLevel);
			levelMap.put(minecraftLevel.dimension().location().toString(), level);
			updateWorldList();
			new AttachWorldDataEvent(level).invoke();
		}

		return level;
	}

	@Deprecated(forRemoval = true)
	@ApiStatus.ScheduledForRemoval(inVersion = "4.1")
	public final WorldJS getWorld(Level minecraftLevel) {
		return getLevel(minecraftLevel);
	}

	@Nullable
	public ServerPlayerJS getPlayer(UUID uuid) {
		var p = playerMap.get(uuid);

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

		var uuid = UUIDWrapper.fromString(name);

		if (uuid != null) {
			return getPlayer(uuid);
		}

		for (var p : playerMap.values()) {
			if (p.getName().equalsIgnoreCase(name)) {
				return p.getPlayer();
			}
		}

		for (var p : playerMap.values()) {
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
		var list = new EntityArrayList(overworld, 10);

		for (var level : allLevels) {
			list.addAll(level.getEntities());
		}

		return list;
	}

	public EntityArrayList getEntities(String filter) {
		var list = new EntityArrayList(overworld, 10);

		for (var level : allLevels) {
			list.addAll(level.getEntities(filter));
		}

		return list;
	}

	public ScheduledEvent schedule(long timer, @Nullable Object data, IScheduledEventCallback event) {
		var e = new ScheduledEvent(this, false, timer, System.currentTimeMillis() + timer, data, event);
		scheduledEvents.add(e);
		return e;
	}

	public ScheduledEvent schedule(long timer, IScheduledEventCallback event) {
		return schedule(timer, null, event);
	}

	public ScheduledEvent scheduleInTicks(long ticks, @Nullable Object data, IScheduledEventCallback event) {
		var e = new ScheduledEvent(this, true, ticks, overworld.getTime() + ticks, data, event);
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
		var a = getMinecraftServer().getAdvancements().getAdvancement(id);
		return a == null ? null : new AdvancementJS(a);
	}

	public void sendDataToAll(String channel, @Nullable CompoundTag data) {
		new SendDataFromServerMessage(channel, data).sendToAll(getMinecraftServer());
	}
}
