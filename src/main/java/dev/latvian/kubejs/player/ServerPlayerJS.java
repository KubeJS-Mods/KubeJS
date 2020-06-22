package dev.latvian.kubejs.player;

import dev.latvian.kubejs.core.PlayerInteractionManagerKJS;
import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.net.KubeJSNet;
import dev.latvian.kubejs.net.MessageCloseOverlay;
import dev.latvian.kubejs.net.MessageOpenOverlay;
import dev.latvian.kubejs.net.MessageSendDataFromServer;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.text.TextTranslate;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.Overlay;
import dev.latvian.kubejs.world.ServerWorldJS;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SHeldItemChangePacket;
import net.minecraft.server.management.ProfileBanEntry;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.Date;

/**
 * @author LatvianModder
 */
public class ServerPlayerJS extends PlayerJS<ServerPlayerEntity>
{
	public final ServerJS server;
	private final boolean hasClientMod;

	public ServerPlayerJS(ServerPlayerDataJS d, ServerWorldJS w, ServerPlayerEntity p)
	{
		super(d, w, p);
		server = w.getServer();
		hasClientMod = d.hasClientMod();
	}

	@Override
	public PlayerStatsJS getStats()
	{
		return new PlayerStatsJS(this, minecraftPlayer.getStats());
	}

	@Override
	public void openOverlay(Overlay overlay)
	{
		KubeJSNet.MAIN.send(PacketDistributor.PLAYER.with(() -> minecraftPlayer), new MessageOpenOverlay(overlay));
	}

	@Override
	public void closeOverlay(String overlay)
	{
		KubeJSNet.MAIN.send(PacketDistributor.PLAYER.with(() -> minecraftPlayer), new MessageCloseOverlay(overlay));
	}

	@Override
	public boolean isMiningBlock()
	{
		return ((PlayerInteractionManagerKJS) minecraftPlayer.interactionManager).isDestroyingBlockKJS();
	}

	public void setCreativeMode(boolean mode)
	{
		minecraftPlayer.interactionManager.setGameType(mode ? GameType.CREATIVE : GameType.SURVIVAL);
	}

	public void setGameMode(String mode)
	{
		if (mode.equals("survival"))
		{
			minecraftPlayer.interactionManager.setGameType(GameType.SURVIVAL);
		}
		else if (mode.equals("creative"))
		{
			minecraftPlayer.interactionManager.setGameType(GameType.CREATIVE);
		}
		else if (mode.equals("adventure"))
		{
			minecraftPlayer.interactionManager.setGameType(GameType.ADVENTURE);
		}
		else if (mode.equals("spectator"))
		{
			minecraftPlayer.interactionManager.setGameType(GameType.SPECTATOR);
		}
	}


	public boolean isOP()
	{
		return server.minecraftServer.getPlayerList().canSendCommands(minecraftPlayer.getGameProfile());
	}

	public void kick(Object reason)
	{
		minecraftPlayer.connection.disconnect(Text.of(reason).component());
	}

	public void kick()
	{
		kick(new TextTranslate("multiplayer.disconnect.kicked"));
	}

	public void ban(String banner, String reason, long expiresInMillis)
	{
		Date date = new Date();
		ProfileBanEntry userlistbansentry = new ProfileBanEntry(minecraftPlayer.getGameProfile(), date, banner, new Date(date.getTime() + (expiresInMillis <= 0L ? 315569260000L : expiresInMillis)), reason);
		server.minecraftServer.getPlayerList().getBannedPlayers().addEntry(userlistbansentry);
		kick(new TextTranslate("multiplayer.disconnect.banned"));
	}

	public boolean hasClientMod()
	{
		return hasClientMod;
	}

	public void unlockAdvancement(@ID String id)
	{
		AdvancementJS a = ServerJS.instance.getAdvancement(id);

		if (a != null)
		{
			AdvancementProgress advancementprogress = minecraftPlayer.getAdvancements().getProgress(a.advancement);

			for (String s : advancementprogress.getRemaningCriteria())
			{
				minecraftPlayer.getAdvancements().grantCriterion(a.advancement, s);
			}
		}
	}

	public void revokeAdvancement(@ID String id)
	{
		AdvancementJS a = ServerJS.instance.getAdvancement(id);

		if (a != null)
		{
			AdvancementProgress advancementprogress = minecraftPlayer.getAdvancements().getProgress(a.advancement);

			if (advancementprogress.hasProgress())
			{
				for (String s : advancementprogress.getCompletedCriteria())
				{
					minecraftPlayer.getAdvancements().revokeCriterion(a.advancement, s);
				}
			}
		}
	}

	@Override
	public void setSelectedSlot(int index)
	{
		int p = getSelectedSlot();
		super.setSelectedSlot(index);
		int n = getSelectedSlot();

		if (p != n && minecraftPlayer.connection != null)
		{
			minecraftPlayer.connection.sendPacket(new SHeldItemChangePacket(n));
		}
	}

	@Override
	public void setMouseItem(Object item)
	{
		super.setMouseItem(item);

		if (minecraftPlayer.connection != null)
		{
			minecraftPlayer.updateHeldItem();
		}
	}

	@Override
	public void sendData(String channel, @Nullable Object data)
	{
		if (!channel.isEmpty())
		{
			KubeJSNet.MAIN.send(PacketDistributor.PLAYER.with(() -> minecraftPlayer), new MessageSendDataFromServer(channel, MapJS.nbt(data)));
		}
	}
}