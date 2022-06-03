package dev.latvian.mods.kubejs.server;

import dev.latvian.mods.kubejs.core.PlayerSelector;
import dev.latvian.mods.kubejs.level.LevelJS;
import dev.latvian.mods.kubejs.level.ServerLevelJS;
import dev.latvian.mods.kubejs.net.SendDataFromServerMessage;
import dev.latvian.mods.kubejs.player.AdvancementJS;
import dev.latvian.mods.kubejs.player.EntityArrayList;
import dev.latvian.mods.kubejs.player.FakeServerPlayerDataJS;
import dev.latvian.mods.kubejs.player.ServerPlayerDataJS;
import dev.latvian.mods.kubejs.player.ServerPlayerJS;
import dev.latvian.mods.kubejs.script.AttachDataEvent;
import dev.latvian.mods.kubejs.util.AttachedData;
import dev.latvian.mods.kubejs.util.MessageSender;
import dev.latvian.mods.kubejs.util.WithAttachedData;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
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
	public final transient Map<ResourceLocation, ServerLevelJS> levelMap;
	public final transient Map<UUID, ServerPlayerDataJS> playerMap;
	public final transient Map<UUID, FakeServerPlayerDataJS> fakePlayerMap;
	public final transient List<ServerLevelJS> allLevels;
	public final CompoundTag persistentData;

	public ServerLevelJS overworld;
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

	public List<ServerLevelJS> getAllLevels() {
		return allLevels;
	}

	public ServerLevelJS getOverworld() {
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
	public Component getName() {
		return new TextComponent(getMinecraftServer().name());
	}

	@Override
	public Component getDisplayName() {
		return getMinecraftServer().createCommandSourceStack().getDisplayName();
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

	public LevelJS getLevel(ResourceLocation dimension) {
		var level = levelMap.get(dimension);

		if (level != null) {
			return level;
		}

		var minecraftLevel = getMinecraftServer().getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, dimension));
		if (minecraftLevel == null) {
			return null;
		} else {
			return wrapMinecraftLevel(minecraftLevel);
		}
	}

	// If you're a script dev, use Level.asKJS() instead,
	// I'm too lazy to make a wrapper for it
	@HideFromJS
	public LevelJS wrapMinecraftLevel(Level minecraftLevel) {
		var level = levelMap.get(minecraftLevel.dimension().location());

		if (level == null) {
			level = new ServerLevelJS(this, (ServerLevel) minecraftLevel);
			levelMap.put(minecraftLevel.dimension().location(), level);
			updateWorldList();
			AttachDataEvent.forLevel(level).invoke();
		}

		return level;
	}

	@Nullable
	public ServerPlayerJS getPlayer(PlayerSelector selector) {
		return selector.getPlayer(playerMap);
	}

	@Nullable
	public ServerPlayerJS getFakePlayer(PlayerSelector selector) {
		return selector.getPlayer(fakePlayerMap);
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
