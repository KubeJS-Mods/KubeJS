package dev.latvian.kubejs.net;

import dev.latvian.kubejs.stages.Stages;
import me.shedaniel.architectury.networking.NetworkManager.PacketContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class MessageAddStage {
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

	void write(FriendlyByteBuf buf) {
		buf.writeUUID(player);
		buf.writeUtf(stage, Short.MAX_VALUE);
	}

	void handle(Supplier<PacketContext> context) {
		context.get().queue(() -> {
			Player p0 = context.get().getPlayer();
			Player p = player.equals(p0.getUUID()) ? p0 : p0.level.getPlayerByUUID(player);

			if (p != null) {
				Stages.get(p).add(stage);
			}
		});
	}
}