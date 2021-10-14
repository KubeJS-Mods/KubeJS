package dev.latvian.kubejs.net;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.mods.rhino.mod.util.NBTUtils;
import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

/**
 * @author LatvianModder
 */
public class PaintMessage extends BaseS2CMessage {
	private final CompoundTag tag;

	public PaintMessage(CompoundTag c) {
		tag = c;
	}

	PaintMessage(FriendlyByteBuf buffer) {
		tag = NBTUtils.read(buffer);
	}

	@Override
	public MessageType getType() {
		return KubeJSNet.PAINT;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeNbt(tag);
	}

	@Override
	public void handle(PacketContext context) {
		KubeJS.PROXY.paint(tag);
	}
}