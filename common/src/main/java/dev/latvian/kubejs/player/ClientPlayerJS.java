package dev.latvian.kubejs.player;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.entity.RayTraceResultJS;
import dev.latvian.kubejs.net.SendDataFromClientMessage;
import dev.latvian.kubejs.util.MapJS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public class ClientPlayerJS extends PlayerJS<Player> {
	private final boolean isSelf;

	public ClientPlayerJS(ClientPlayerDataJS d, Player p, boolean s) {
		super(d, d.getWorld(), p);
		isSelf = s;
	}

	public boolean isSelf() {
		return isSelf;
	}

	@Override
	public PlayerStatsJS getStats() {
		if (!isSelf()) {
			throw new IllegalStateException("Can't access other player stats!");
		}

		return new PlayerStatsJS(this, ((LocalPlayer) minecraftPlayer).getStats());
	}

	@Override
	public void paint(CompoundTag tag) {
		if (isSelf()) {
			KubeJS.PROXY.paint(tag);
		}
	}

	@Override
	public boolean isMiningBlock() {
		return isSelf() && Minecraft.getInstance().gameMode.isDestroying();
	}

	@Override
	public void sendData(String channel, @Nullable Object data) {
		if (!channel.isEmpty() && isSelf()) {
			new SendDataFromClientMessage(channel, MapJS.nbt(data)).sendToServer();
		}
	}

	@Override
	public RayTraceResultJS rayTrace(double distance) {
		return isSelf ? new RayTraceResultJS(this, Minecraft.getInstance().hitResult, distance) : super.rayTrace(distance);
	}
}