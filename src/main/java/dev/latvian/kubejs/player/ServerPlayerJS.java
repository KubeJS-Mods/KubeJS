package dev.latvian.kubejs.player;

import dev.latvian.kubejs.net.KubeJSNetHandler;
import dev.latvian.kubejs.net.MessageCloseOverlay;
import dev.latvian.kubejs.net.MessageOpenOverlay;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.text.TextTranslate;
import dev.latvian.kubejs.util.Overlay;
import dev.latvian.kubejs.world.ServerWorldJS;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.minecraft.server.management.UserListBansEntry;

import java.util.Date;

/**
 * @author LatvianModder
 */
public class ServerPlayerJS extends PlayerJS<EntityPlayerMP>
{
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
		return new PlayerStatsJS(this, getPlayerEntity().getStatFile());
	}

	@Override
	public void openOverlay(Overlay overlay)
	{
		KubeJSNetHandler.net.sendTo(new MessageOpenOverlay(overlay), getPlayerEntity());
	}

	@Override
	public void closeOverlay(String overlay)
	{
		KubeJSNetHandler.net.sendTo(new MessageCloseOverlay(overlay), getPlayerEntity());
	}

	public boolean isOP()
	{
		return server.server.getPlayerList().canSendCommands(getPlayerEntity().getGameProfile());
	}

	public void kick(Text reason)
	{
		getPlayerEntity().connection.disconnect(reason.component());
	}

	public void kick()
	{
		kick(new TextTranslate("multiplayer.disconnect.kicked"));
	}

	public void ban(String banner, String reason, long expiresInMillis)
	{
		Date date = new Date();
		UserListBansEntry userlistbansentry = new UserListBansEntry(getPlayerEntity().getGameProfile(), date, banner, new Date(date.getTime() + (expiresInMillis <= 0L ? 315569260000L : expiresInMillis)), reason);
		server.server.getPlayerList().getBannedPlayers().addEntry(userlistbansentry);
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
			AdvancementProgress advancementprogress = getPlayerEntity().getAdvancements().getProgress(a.advancement);

			for (String s : advancementprogress.getRemaningCriteria())
			{
				getPlayerEntity().getAdvancements().grantCriterion(a.advancement, s);
			}
		}
	}

	public void revokeAdvancement(Object id)
	{
		AdvancementJS a = ServerJS.instance.getAdvancement(id);

		if (a != null)
		{
			AdvancementProgress advancementprogress = getPlayerEntity().getAdvancements().getProgress(a.advancement);

			if (advancementprogress.hasProgress())
			{
				for (String s : advancementprogress.getCompletedCriteria())
				{
					getPlayerEntity().getAdvancements().revokeCriterion(a.advancement, s);
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

		if (p != n && getPlayerEntity().connection != null)
		{
			getPlayerEntity().connection.sendPacket(new SPacketHeldItemChange(n));
		}
	}

	@Override
	public void setMouseItem(Object item)
	{
		super.setMouseItem(item);

		if (getPlayerEntity().connection != null)
		{
			getPlayerEntity().updateHeldItem();
		}
	}
}