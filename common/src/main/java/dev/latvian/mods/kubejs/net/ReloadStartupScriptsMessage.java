package dev.latvian.mods.kubejs.net;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.network.FriendlyByteBuf;

public class ReloadStartupScriptsMessage extends BaseS2CMessage {
	public final boolean dedicated;

	public ReloadStartupScriptsMessage(boolean dedicated) {
		this.dedicated = dedicated;
	}

	ReloadStartupScriptsMessage(FriendlyByteBuf buf) {
		dedicated = buf.readBoolean();
	}

	@Override
	public MessageType getType() {
		return KubeJSNet.RELOAD_STARTUP_SCRIPTS;
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeBoolean(dedicated);
	}

	@Override
	public void handle(PacketContext context) {
		context.queue(() -> KubeJS.PROXY.reloadStartupScripts(dedicated));
	}
}