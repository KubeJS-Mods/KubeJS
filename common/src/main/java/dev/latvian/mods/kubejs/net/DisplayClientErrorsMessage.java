package dev.latvian.mods.kubejs.net;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.network.FriendlyByteBuf;

public class DisplayClientErrorsMessage extends BaseS2CMessage {
	public DisplayClientErrorsMessage() {
	}

	DisplayClientErrorsMessage(FriendlyByteBuf buf) {
	}

	@Override
	public MessageType getType() {
		return KubeJSNet.DISPLAY_CLIENT_ERRORS;
	}

	@Override
	public void write(FriendlyByteBuf buf) {
	}

	@Override
	public void handle(PacketContext context) {
		KubeJS.PROXY.openErrors(ScriptType.CLIENT);
	}
}