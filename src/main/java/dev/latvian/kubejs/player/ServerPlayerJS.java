package dev.latvian.kubejs.player;

import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.text.TextTranslate;
import dev.latvian.kubejs.world.ServerWorldJS;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.UserListBansEntry;

import java.util.Date;

/**
 * @author LatvianModder
 */
public class ServerPlayerJS extends PlayerJS<EntityPlayerMP>
{
	public final ServerJS server;

	public ServerPlayerJS(ServerPlayerDataJS d, ServerWorldJS w, EntityPlayerMP p)
	{
		super(d, w, p);
		server = w.server;
	}

	@Override
	public PlayerStatsJS getStats()
	{
		return new PlayerStatsJS(this, playerEntity.getStatFile());
	}

	public boolean isOP()
	{
		return server.server.getPlayerList().canSendCommands(playerEntity.getGameProfile());
	}

	public void kick(Text reason)
	{
		playerEntity.connection.disconnect(reason.component());
	}

	public void kick()
	{
		kick(new TextTranslate("multiplayer.disconnect.kicked"));
	}

	public void ban(String banner, String reason, long expiresInMillis)
	{
		Date date = new Date();
		UserListBansEntry userlistbansentry = new UserListBansEntry(playerEntity.getGameProfile(), date, banner, new Date(date.getTime() + (expiresInMillis <= 0L ? 315569260000L : expiresInMillis)), reason);
		server.server.getPlayerList().getBannedPlayers().addEntry(userlistbansentry);
		kick(new TextTranslate("multiplayer.disconnect.banned"));
	}
}