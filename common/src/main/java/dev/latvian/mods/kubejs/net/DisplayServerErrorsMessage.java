package dev.latvian.mods.kubejs.net;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.script.ConsoleLine;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.network.FriendlyByteBuf;

import java.util.List;

public class DisplayServerErrorsMessage extends BaseS2CMessage {
	private final ScriptType type;
	private final List<ConsoleLine> errors, warnings;

	public DisplayServerErrorsMessage(ScriptType type, List<ConsoleLine> errors, List<ConsoleLine> warnings) {
		this.type = type;
		this.errors = errors;
		this.warnings = warnings;
	}

	DisplayServerErrorsMessage(FriendlyByteBuf buf) {
		this.type = ScriptType.values()[buf.readByte()];
		this.errors = buf.readList(ConsoleLine::new);
		this.warnings = buf.readList(ConsoleLine::new);
	}

	@Override
	public MessageType getType() {
		return KubeJSNet.DISPLAY_SERVER_ERRORS;
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeByte(type.ordinal());
		buf.writeCollection(errors, ConsoleLine::writeToNet);
		buf.writeCollection(warnings, ConsoleLine::writeToNet);
	}

	@Override
	public void handle(PacketContext context) {
		KubeJS.PROXY.openErrors(type, errors, warnings);
	}
}