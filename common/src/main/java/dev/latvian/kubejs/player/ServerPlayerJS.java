package dev.latvian.kubejs.player;

import dev.latvian.kubejs.core.PlayerInteractionManagerKJS;
import dev.latvian.kubejs.net.KubeJSNet;
import dev.latvian.kubejs.net.MessageCloseOverlay;
import dev.latvian.kubejs.net.MessageOpenOverlay;
import dev.latvian.kubejs.net.MessageSendDataFromServer;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.text.TextTranslate;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.Overlay;
import dev.latvian.kubejs.world.BlockContainerJS;
import dev.latvian.kubejs.world.ServerWorldJS;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.UserBanListEntry;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

/**
 * @author LatvianModder
 */
public class ServerPlayerJS extends PlayerJS<ServerPlayer>
{
	public final ServerJS server;
	private final boolean hasClientMod;

	public ServerPlayerJS(ServerPlayerDataJS d, ServerWorldJS w, ServerPlayer p)
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
		KubeJSNet.MAIN.sendToPlayer(minecraftPlayer, new MessageOpenOverlay(overlay));
	}

	@Override
	public void closeOverlay(String overlay)
	{
		KubeJSNet.MAIN.sendToPlayer(minecraftPlayer, new MessageCloseOverlay(overlay));
	}

	@Override
	public boolean isMiningBlock()
	{
		return ((PlayerInteractionManagerKJS) minecraftPlayer.gameMode).isDestroyingBlockKJS();
	}

	public void setCreativeMode(boolean mode)
	{
		minecraftPlayer.gameMode.setGameModeForPlayer(mode ? GameType.CREATIVE : GameType.SURVIVAL);
	}

	public void setGameMode(String mode)
	{
		switch (mode)
		{
			case "survival":
				minecraftPlayer.gameMode.setGameModeForPlayer(GameType.SURVIVAL);
				break;
			case "creative":
				minecraftPlayer.gameMode.setGameModeForPlayer(GameType.CREATIVE);
				break;
			case "adventure":
				minecraftPlayer.gameMode.setGameModeForPlayer(GameType.ADVENTURE);
				break;
			case "spectator":
				minecraftPlayer.gameMode.setGameModeForPlayer(GameType.SPECTATOR);
				break;
		}
	}

	public boolean isOP()
	{
		return server.getMinecraftServer().getPlayerList().isOp(minecraftPlayer.getGameProfile());
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
		UserBanListEntry userlistbansentry = new UserBanListEntry(minecraftPlayer.getGameProfile(), date, banner, new Date(date.getTime() + (expiresInMillis <= 0L ? 315569260000L : expiresInMillis)), reason);
		server.getMinecraftServer().getPlayerList().getBans().add(userlistbansentry);
		kick(new TextTranslate("multiplayer.disconnect.banned"));
	}

	public boolean hasClientMod()
	{
		return hasClientMod;
	}

	public boolean isAdvancementDone(ResourceLocation id)
	{
		AdvancementJS a = ServerJS.instance.getAdvancement(id);
		return a != null && minecraftPlayer.getAdvancements().getOrStartProgress(a.advancement).isDone();
	}

	public void unlockAdvancement(ResourceLocation id)
	{
		AdvancementJS a = ServerJS.instance.getAdvancement(id);

		if (a != null)
		{
			AdvancementProgress advancementprogress = minecraftPlayer.getAdvancements().getOrStartProgress(a.advancement);

			for (String s : advancementprogress.getRemainingCriteria())
			{
				minecraftPlayer.getAdvancements().award(a.advancement, s);
			}
		}
	}

	public void revokeAdvancement(ResourceLocation id)
	{
		AdvancementJS a = ServerJS.instance.getAdvancement(id);

		if (a != null)
		{
			AdvancementProgress advancementprogress = minecraftPlayer.getAdvancements().getOrStartProgress(a.advancement);

			if (advancementprogress.hasProgress())
			{
				for (String s : advancementprogress.getCompletedCriteria())
				{
					minecraftPlayer.getAdvancements().revoke(a.advancement, s);
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
			minecraftPlayer.connection.send(new ClientboundSetCarriedItemPacket(n));
		}
	}

	@Override
	public void setMouseItem(Object item)
	{
		super.setMouseItem(item);

		if (minecraftPlayer.connection != null)
		{
			minecraftPlayer.broadcastCarriedItem();
		}
	}

	@Override
	public void sendData(String channel, @Nullable Object data)
	{
		if (!channel.isEmpty())
		{
			KubeJSNet.MAIN.sendToPlayer(minecraftPlayer, new MessageSendDataFromServer(channel, MapJS.nbt(data)));
		}
	}

	@Nullable
	public BlockContainerJS getSpawnLocation()
	{
		BlockPos pos = minecraftPlayer.getRespawnPosition();
		return pos == null ? null : new BlockContainerJS(minecraftPlayer.level, pos);
	}

	public void setSpawnLocation(BlockContainerJS c)
	{
		if (c.minecraftWorld instanceof Level)
		{
			minecraftPlayer.setRespawnPosition(((Level) c.minecraftWorld).dimension(), c.getPos(), 0F, true, false);
		}
	}
}