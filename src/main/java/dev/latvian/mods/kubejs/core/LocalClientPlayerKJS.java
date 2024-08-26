package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.client.KubeSessionData;
import dev.latvian.mods.kubejs.net.SendDataFromClientPayload;
import dev.latvian.mods.kubejs.player.PlayerStatsJS;
import dev.latvian.mods.kubejs.util.NotificationToastData;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS("kjs$")
public interface LocalClientPlayerKJS extends ClientPlayerKJS {
	@Override
	default LocalPlayer kjs$self() {
		return (LocalPlayer) this;
	}

	default Minecraft kjs$getMinecraft() {
		return Minecraft.getInstance();
	}

	@Override
	default void kjs$runCommand(String command) {
		kjs$self().connection.sendCommand(command);
	}

	@Override
	default void kjs$runCommandSilent(String command) {
		kjs$self().connection.sendCommand(command);
	}

	@Override
	default boolean isSelf() {
		return true;
	}

	@Override
	default void kjs$sendData(String channel, @Nullable CompoundTag data) {
		if (!channel.isEmpty()) {
			PacketDistributor.sendToServer(new SendDataFromClientPayload(channel, data));
		}
	}

	@Override
	default PlayerStatsJS kjs$getStats() {
		return new PlayerStatsJS(kjs$self(), kjs$self().getStats());
	}

	@Override
	default boolean kjs$isMiningBlock() {
		return Minecraft.getInstance().gameMode.isDestroying();
	}

	@Override
	default void kjs$notify(NotificationToastData notification) {
		notification.show();
	}

	@Override
	default void kjs$setActivePostShader(@Nullable ResourceLocation id) {
		var sessionData = KubeSessionData.of(kjs$self().connection);

		if (sessionData != null) {
			sessionData.activePostShader = id;
			var mc = kjs$getMinecraft();
			mc.gameRenderer.checkEntityPostEffect(mc.options.getCameraType().isFirstPerson() ? mc.getCameraEntity() : null);
		}
	}
}
