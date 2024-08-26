package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.player.PlayerStatsJS;
import dev.latvian.mods.kubejs.util.NotificationToastData;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS("kjs$")
public interface ClientPlayerKJS extends PlayerKJS {
	@Override
	default AbstractClientPlayer kjs$self() {
		return (AbstractClientPlayer) this;
	}

	default boolean isSelf() {
		return false;
	}

	@Override
	default void kjs$sendData(String channel, @Nullable CompoundTag data) {
	}

	@Override
	default PlayerStatsJS kjs$getStats() {
		throw new IllegalStateException("Can't access other client player stats!");
	}

	@Override
	default boolean kjs$isMiningBlock() {
		return false;
	}

	@Override
	default void kjs$notify(NotificationToastData notification) {
	}
}
