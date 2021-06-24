package dev.latvian.kubejs.net;

import dev.latvian.kubejs.stages.Stages;
import me.shedaniel.architectury.networking.NetworkManager.PacketContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class MessageSyncStages {
	private final UUID player;
	private final Collection<String> stages;

	public MessageSyncStages(UUID p, Collection<String> s) {
		player = p;
		stages = s;
	}

	MessageSyncStages(FriendlyByteBuf buf) {
		player = buf.readUUID();

		int s = buf.readVarInt();
		stages = new ArrayList<>(s);

		for (int i = 0; i < s; i++) {
			stages.add(buf.readUtf(Short.MAX_VALUE));
		}
	}

	void write(FriendlyByteBuf buf) {
		buf.writeUUID(player);
		buf.writeVarInt(stages.size());

		for (String s : stages) {
			buf.writeUtf(s, Short.MAX_VALUE);
		}
	}

	void handle(Supplier<PacketContext> context) {
		context.get().queue(() -> {
			Player p0 = context.get().getPlayer();
			Player p = player.equals(p0.getUUID()) ? p0 : p0.level.getPlayerByUUID(player);

			if (p != null) {
				Stages.get(p).replace(stages);
			}
		});
	}
}