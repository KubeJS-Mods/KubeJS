package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.level.LevelJS;
import dev.latvian.mods.kubejs.level.ServerLevelJS;
import dev.latvian.mods.kubejs.net.SendDataFromServerMessage;
import dev.latvian.mods.kubejs.player.AdvancementJS;
import dev.latvian.mods.kubejs.player.EntityArrayList;
import dev.latvian.mods.kubejs.player.FakeServerPlayerDataJS;
import dev.latvian.mods.kubejs.player.ServerPlayerDataJS;
import dev.latvian.mods.kubejs.player.ServerPlayerJS;
import dev.latvian.mods.kubejs.script.AttachDataEvent;
import dev.latvian.mods.kubejs.server.IScheduledEventCallback;
import dev.latvian.mods.kubejs.server.ScheduledEvent;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RemapPrefixForJS("kjs$")
public interface MinecraftServerKJS extends AsKJS<MinecraftServer>, WithAttachedData, MessageSenderKJS, WithPersistentData {
	@Override
	default MinecraftServer asKJS() {
		return kjs$self();
	}

	MinecraftServer kjs$self();

	MinecraftServer.ReloadableResources kjs$getReloadableResources();

	Map<ResourceLocation, ServerLevelJS> kjs$getLevelMap();

	Map<UUID, ServerPlayerDataJS> kjs$getPlayerMap();

	Map<UUID, FakeServerPlayerDataJS> kjs$getFakePlayerMap();

	List<ServerLevelJS> kjs$getAllLevels();

	LevelJS kjs$getOverworld();

	ScheduledEvent kjs$schedule(long timer, IScheduledEventCallback event);

	ScheduledEvent kjs$scheduleInTicks(long ticks, IScheduledEventCallback event);

	default void kjs$updateWorldList() {
		kjs$getAllLevels().clear();
		kjs$getAllLevels().addAll(kjs$getLevelMap().values());
	}

	@Override
	default Component kjs$getName() {
		return Component.literal(kjs$self().name());
	}

	@Override
	default Component kjs$getDisplayName() {
		return kjs$self().createCommandSourceStack().getDisplayName();
	}

	@Override
	default void kjs$tell(Component message) {
		kjs$self().sendSystemMessage(message);

		for (var player : kjs$self().getPlayerList().getPlayers()) {
			player.sendSystemMessage(message);
		}
	}

	@Override
	default void kjs$setStatusMessage(Component message) {
		for (var player : kjs$self().getPlayerList().getPlayers()) {
			player.displayClientMessage(message, true);
		}
	}

	@Override
	default int kjs$runCommand(String command) {
		return kjs$self().getCommands().performCommand(kjs$self().createCommandSourceStack(), command);
	}

	@Override
	default int kjs$runCommandSilent(String command) {
		return kjs$self().getCommands().performCommand(kjs$self().createCommandSourceStack().withSuppressedOutput(), command);
	}

	default ServerLevelJS kjs$getLevel(ResourceLocation dimension) {
		var level = kjs$getLevelMap().get(dimension);

		if (level != null) {
			return level;
		}

		var minecraftLevel = kjs$self().getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, dimension));
		if (minecraftLevel == null) {
			return null;
		} else {
			return kjs$wrapMinecraftLevel(minecraftLevel);
		}
	}

	// If you're a script dev, use Level.asKJS() instead,
	// I'm too lazy to make a wrapper for it
	@HideFromJS
	default ServerLevelJS kjs$wrapMinecraftLevel(Level minecraftLevel) {
		var level = kjs$getLevelMap().get(minecraftLevel.dimension().location());

		if (level == null) {
			level = new ServerLevelJS(kjs$self(), (ServerLevel) minecraftLevel);
			kjs$getLevelMap().put(minecraftLevel.dimension().location(), level);
			kjs$updateWorldList();
			AttachDataEvent.forLevel(level).invoke();
		}

		return level;
	}

	@Nullable
	default ServerPlayerJS kjs$getPlayer(PlayerSelector selector) {
		return selector.getPlayer(kjs$getPlayerMap());
	}

	@Nullable
	default ServerPlayerJS kjs$getFakePlayer(PlayerSelector selector) {
		return selector.getPlayer(kjs$getFakePlayerMap());
	}

	default EntityArrayList kjs$getPlayers() {
		return new EntityArrayList(kjs$getOverworld(), kjs$self().getPlayerList().getPlayers());
	}

	default EntityArrayList kjs$getEntities() {
		var list = new EntityArrayList(kjs$getOverworld(), 10);

		for (var level : kjs$getAllLevels()) {
			list.addAll(level.getEntities());
		}

		return list;
	}

	default EntityArrayList kjs$getEntities(String filter) {
		var list = new EntityArrayList(kjs$getOverworld(), 10);

		for (var level : kjs$getAllLevels()) {
			list.addAll(level.getEntities(filter));
		}

		return list;
	}

	@Nullable
	default AdvancementJS kjs$getAdvancement(ResourceLocation id) {
		var a = kjs$self().getAdvancements().getAdvancement(id);
		return a == null ? null : new AdvancementJS(a);
	}

	default void kjs$sendDataToAll(String channel, @Nullable CompoundTag data) {
		new SendDataFromServerMessage(channel, data).sendToAll(kjs$self());
	}
}
