package dev.latvian.mods.kubejs.stages;

import dev.latvian.mods.kubejs.bindings.event.PlayerEvents;
import dev.latvian.mods.kubejs.net.AddStagePayload;
import dev.latvian.mods.kubejs.net.RemoveStagePayload;
import dev.latvian.mods.kubejs.net.SyncStagesPayload;
import dev.latvian.mods.kubejs.player.StageChangedEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Collection;

public interface Stages {
	Player getPlayer();

	boolean addNoUpdate(String stage);

	boolean removeNoUpdate(String stage);

	Collection<String> getAll();

	default boolean has(String stage) {
		return getAll().contains(stage);
	}

	default boolean add(String stage) {
		if (addNoUpdate(stage)) {
			if (getPlayer() instanceof ServerPlayer player) {
				PacketDistributor.sendToAllPlayers(new AddStagePayload(player.getUUID(), stage));
			}

			if (PlayerEvents.STAGE_ADDED.hasListeners(stage)) {
				PlayerEvents.STAGE_ADDED.post(new StageChangedEvent(getPlayer(), this, stage), stage);
			}

			return true;
		}

		return false;
	}

	default boolean remove(String stage) {
		if (removeNoUpdate(stage)) {
			if (getPlayer() instanceof ServerPlayer player) {
				PacketDistributor.sendToAllPlayers(new RemoveStagePayload(player.getUUID(), stage));
			}

			if (PlayerEvents.STAGE_REMOVED.hasListeners(stage)) {
				PlayerEvents.STAGE_REMOVED.post(new StageChangedEvent(getPlayer(), this, stage), stage);
			}

			return true;
		}

		return false;
	}

	default boolean set(String stage, boolean enabled) {
		return enabled ? add(stage) : remove(stage);
	}

	default boolean toggle(String stage) {
		return set(stage, !has(stage));
	}

	default boolean clear() {
		var all = getAll();

		if (all.isEmpty()) {
			return false;
		}

		for (var s : new ArrayList<>(all)) {
			remove(s);
		}

		return true;
	}

	default void sync() {
		if (getPlayer() instanceof ServerPlayer player) {
			PacketDistributor.sendToPlayer(player, new SyncStagesPayload(getAll()));
		}
	}

	default void replace(Collection<String> stages) {
		var all = getAll();

		for (var s : new ArrayList<>(all)) {
			removeNoUpdate(s);
		}

		for (var s : stages) {
			addNoUpdate(s);
		}

		sync();
	}
}
