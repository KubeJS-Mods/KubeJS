package dev.latvian.mods.kubejs.net;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import dev.latvian.mods.kubejs.stages.Stages;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

/**
 * @author LatvianModder
 */
public class RemoveStageMessage extends BaseS2CMessage {
	private final UUID player;
	private final String stage;

	public RemoveStageMessage(UUID p, String s) {
		player = p;
		stage = s;
	}

	RemoveStageMessage(FriendlyByteBuf buf) {
		player = buf.readUUID();
		stage = buf.readUtf(Short.MAX_VALUE);
	}

	@Override
	public MessageType getType() {
		return KubeJSNet.REMOVE_STAGE;
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeUUID(player);
		buf.writeUtf(stage, Short.MAX_VALUE);
	}

	@Override
	public void handle(PacketContext context) {
		Player p0 = context.getPlayer();
		Player p = player.equals(p0.getUUID()) ? p0 : p0.level.getPlayerByUUID(player);

		if (p != null) {
			Stages.get(p).remove(stage);
		}
	}
}