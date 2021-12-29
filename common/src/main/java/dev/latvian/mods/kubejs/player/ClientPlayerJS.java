package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.entity.RayTraceResultJS;
import dev.latvian.mods.kubejs.net.SendDataFromClientMessage;
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
		super(d, d.getLevel(), p);
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
	public void sendData(String channel, @Nullable CompoundTag data) {
		if (!channel.isEmpty() && isSelf()) {
			new SendDataFromClientMessage(channel, data).sendToServer();
		}
	}

	@Override
	public RayTraceResultJS rayTrace(double distance) {
		return isSelf ? new RayTraceResultJS(this, Minecraft.getInstance().hitResult, distance) : super.rayTrace(distance);
	}
}