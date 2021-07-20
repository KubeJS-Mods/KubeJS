package dev.latvian.kubejs.net;

import dev.latvian.kubejs.stages.Stages;
import me.shedaniel.architectury.networking.NetworkManager.PacketContext;
import me.shedaniel.architectury.networking.simple.BaseS2CMessage;
import me.shedaniel.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

/**
 * @author LatvianModder
 */
public class MessageAddStage extends BaseS2CMessage {
	private final UUID player;
	private final String stage;

	public MessageAddStage(UUID p, String s) {
		player = p;
		stage = s;
	}

	MessageAddStage(FriendlyByteBuf buf) {
		player = buf.readUUID();
		stage = buf.readUtf(Short.MAX_VALUE);
	}

	@Override
	public MessageType getType() {
		return KubeJSNet.ADD_STAGE;
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
			Stages.get(p).add(stage);
		}
	}
}