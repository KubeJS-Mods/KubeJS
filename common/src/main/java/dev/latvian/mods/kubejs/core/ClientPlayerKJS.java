package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.net.SendDataFromClientMessage;
import dev.latvian.mods.kubejs.player.PlayerStatsJS;
import dev.latvian.mods.kubejs.util.NotificationBuilder;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS("kjs$")
public interface ClientPlayerKJS extends PlayerKJS {
	@Override
	default AbstractClientPlayer kjs$self() {
		return (AbstractClientPlayer) this;
	}

	default boolean isSelf() {
		return kjs$self() == KubeJS.PROXY.getClientPlayer();
	}

	@Override
	default void kjs$sendData(String channel, @Nullable CompoundTag data) {
		if (!channel.isEmpty()) {
			new SendDataFromClientMessage(channel, data).sendToServer();
		}
	}

	@Override
	default void kjs$paint(CompoundTag tag) {
		if (isSelf()) {
			KubeJS.PROXY.paint(tag);
		}
	}

	@Override
	default PlayerStatsJS kjs$getStats() {
		if (!isSelf()) {
			throw new IllegalStateException("Can't access other client player stats!");
		}

		return new PlayerStatsJS(kjs$self(), ((LocalPlayer) kjs$self()).getStats());
	}

	@Override
	default boolean kjs$isMiningBlock() {
		return isSelf() && Minecraft.getInstance().gameMode.isDestroying();
	}

	@Override
	default void kjs$notify(NotificationBuilder notification) {
		notification.show();
	}
}
