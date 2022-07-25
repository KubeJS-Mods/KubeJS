package dev.latvian.mods.kubejs.net;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class SyncStagesMessage extends BaseS2CMessage {
	private final UUID player;
	private final Collection<String> stages;

	public SyncStagesMessage(UUID p, Collection<String> s) {
		player = p;
		stages = s;
	}

	SyncStagesMessage(FriendlyByteBuf buf) {
		player = buf.readUUID();

		var s = buf.readVarInt();
		stages = new ArrayList<>(s);

		for (var i = 0; i < s; i++) {
			stages.add(buf.readUtf());
		}
	}

	@Override
	public MessageType getType() {
		return KubeJSNet.SYNC_STAGES;
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeUUID(player);
		buf.writeVarInt(stages.size());

		for (var s : stages) {
			buf.writeUtf(s);
		}
	}

	@Override
	public void handle(PacketContext context) {
		var p0 = context.getPlayer();
		var p = player.equals(p0.getUUID()) ? p0 : p0.level.getPlayerByUUID(player);

		if (p != null) {
			p.kjs$getStages().replace(stages);
		}
	}
}