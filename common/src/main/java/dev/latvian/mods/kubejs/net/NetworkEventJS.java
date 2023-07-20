package dev.latvian.mods.kubejs.net;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import dev.latvian.mods.kubejs.typings.JsInfo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

@JsInfo("""
		Invoked when a network packet is received.
				
		Note that the behaviour of this event is depending on the **script type**.
				
		In `server_scripts`, this event is invoked on the server side when a packet is received from a client.
				
		In `client_scripts`, this event is invoked on the client side when a packet is received from the server.
		""")
public class NetworkEventJS extends PlayerEventJS {
	private final Player player;
	private final String channel;
	private final CompoundTag data;

	public NetworkEventJS(Player p, String c, @Nullable CompoundTag d) {
		player = p;
		channel = c;
		data = d;
	}

	@Override
	@JsInfo("The player that sent the packet. Always `Minecraft.player` in `client_scripts`.")
	public Player getEntity() {
		return player;
	}

	@JsInfo("The channel of the packet.")
	public String getChannel() {
		return channel;
	}

	@Nullable
	@JsInfo("The data of the packet.")
	public CompoundTag getData() {
		return data;
	}
}