package dev.latvian.mods.kubejs.net;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import dev.latvian.mods.kubejs.bindings.event.ItemEvents;
import dev.latvian.mods.kubejs.item.ItemClickedEventJS;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;

/**
 * @author LatvianModder
 */
public class FirstClickMessage extends BaseC2SMessage {
	private final int type;

	public FirstClickMessage(int t) {
		type = t;
	}

	FirstClickMessage(FriendlyByteBuf buf) {
		type = buf.readByte();
	}

	@Override
	public MessageType getType() {
		return KubeJSNet.FIRST_CLICK;
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeByte(type);
	}

	@Override
	public void handle(PacketContext context) {
		if (context.getPlayer() instanceof ServerPlayer serverPlayer) {
			if (type == 0) {
				ItemEvents.FIRST_LEFT_CLICKED.post(new ItemClickedEventJS(serverPlayer, InteractionHand.MAIN_HAND));
			} else if (type == 1) {
				for (var hand : InteractionHand.values()) {
					ItemEvents.FIRST_RIGHT_CLICKED.post(new ItemClickedEventJS(serverPlayer, hand));
				}
			}
		}
	}
}