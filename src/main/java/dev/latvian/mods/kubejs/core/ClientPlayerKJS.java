package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.player.PlayerStatsJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.ThisIs;
import dev.latvian.mods.kubejs.util.NotificationToastData;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS("kjs$")
public interface ClientPlayerKJS extends PlayerKJS {
	@Override
	@HideFromJS
	default AbstractClientPlayer kjs$self() {
		return (AbstractClientPlayer) this;
	}

	@Override
	@ThisIs(LocalPlayer.class)
	@Info("Checks, whether the entity is a reference to yourself - that is - the client player you are controlling.")
	default boolean kjs$isSelf() {
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
