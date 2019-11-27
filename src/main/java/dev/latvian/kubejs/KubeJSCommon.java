package dev.latvian.kubejs;

import dev.latvian.kubejs.net.KubeJSNet;
import dev.latvian.kubejs.net.MessageSendDataFromClient;
import dev.latvian.kubejs.util.Overlay;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.io.File;

/**
 * @author LatvianModder
 */
public class KubeJSCommon
{
	public void init(File folder)
	{
	}

	public void sendData(PlayerEntity playerEntity, String channel, @Nullable CompoundNBT data)
	{
		if (playerEntity instanceof ServerPlayerEntity && !channel.isEmpty())
		{
			final ServerPlayerEntity p = (ServerPlayerEntity) playerEntity;
			KubeJSNet.MAIN.send(PacketDistributor.PLAYER.with(() -> p), new MessageSendDataFromClient(channel, data));
		}
	}

	public void handleDataToClientPacket(String channel, @Nullable CompoundNBT data)
	{
	}

	@Nullable
	public PlayerEntity getClientPlayer()
	{
		return null;
	}

	public void openOverlay(Overlay o)
	{
	}

	public void closeOverlay(String id)
	{
	}
}