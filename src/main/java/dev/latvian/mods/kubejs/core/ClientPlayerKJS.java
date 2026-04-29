package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.player.PlayerStatsJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.ThisIs;
import dev.latvian.mods.kubejs.util.NotificationToastData;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.nbt.CompoundTag;
import org.jspecify.annotations.Nullable;

@RemapPrefixForJS("kjs$")
public interface ClientPlayerKJS extends PlayerKJS {
	@Override
	@HideFromJS
	default AbstractClientPlayer kjs$self() {
		return (AbstractClientPlayer) this;
	}

	@ThisIs(AbstractClientPlayer.class)
	@Info("Checks if the entity is a client-side player.")
	default boolean kjs$isClientPlayer() {
		return true;
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
