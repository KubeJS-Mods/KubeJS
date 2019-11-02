package dev.latvian.kubejs.player;

import dev.latvian.kubejs.net.KubeJSNetHandler;
import dev.latvian.kubejs.net.MessageCloseOverlay;
import dev.latvian.kubejs.net.MessageOpenOverlay;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.text.TextTranslate;
import dev.latvian.kubejs.util.FieldJS;
import dev.latvian.kubejs.util.Overlay;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.world.ServerWorldJS;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.server.management.UserListBansEntry;

import java.util.Date;

/**
 * @author LatvianModder
 */
public class ServerPlayerJS extends PlayerJS<EntityPlayerMP>
{
	private static FieldJS isDestroyingBlockField;

	public final ServerJS server;
	private final boolean hasClientMod;

	public ServerPlayerJS(ServerPlayerDataJS d, ServerWorldJS w, EntityPlayerMP p)
	{
		super(d, w, p);
		server = w.getServer();
		hasClientMod = d.hasClientMod();
	}

	@Override
	public PlayerStatsJS getStats()
	{
		return new PlayerStatsJS(this, minecraftPlayer.getStatFile());
	}

	@Override
	public void openOverlay(Overlay overlay)
	{
		KubeJSNetHandler.net.sendTo(new MessageOpenOverlay(overlay), minecraftPlayer);
	}

	@Override
	public void closeOverlay(String overlay)
	{
		KubeJSNetHandler.net.sendTo(new MessageCloseOverlay(overlay), minecraftPlayer);
	}

	@Override
	public boolean isMiningBlock()
	{
		if (isDestroyingBlockField == null)
		{
			isDestroyingBlockField = UtilsJS.getField(PlayerInteractionManager.class, "isDestroyingBlock", "field_73088_d");
		}

		Object obj = isDestroyingBlockField.get(minecraftPlayer.interactionManager);
		return obj instanceof Boolean && (Boolean) obj;
	}

	public boolean isOP()
	{
		return server.minecraftServer.getPlayerList().canSendCommands(minecraftPlayer.getGameProfile());
	}

	public void kick(Text reason)
	{
		minecraftPlayer.connection.disconnect(reason.component());
	}

	public void kick()
	{
		kick(new TextTranslate("multiplayer.disconnect.kicked"));
	}

	public void ban(String banner, String reason, long expiresInMillis)
	{
		Date date = new Date();
		UserListBansEntry userlistbansentry = new UserListBansEntry(minecraftPlayer.getGameProfile(), date, banner, new Date(date.getTime() + (expiresInMillis <= 0L ? 315569260000L : expiresInMillis)), reason);
		server.minecraftServer.getPlayerList().getBannedPlayers().addEntry(userlistbansentry);
		kick(new TextTranslate("multiplayer.disconnect.banned"));
	}

	public boolean hasClientMod()
	{
		return hasClientMod;
	}

	public void unlockAdvancement(Object id)
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

	public void revokeAdvancement(Object id)
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
			minecraftPlayer.connection.sendPacket(new SPacketHeldItemChange(n));
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
}