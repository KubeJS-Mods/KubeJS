package dev.latvian.kubejs.player;

import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.text.TextTranslate;
import dev.latvian.kubejs.world.ServerWorldJS;
import net.minecraft.entity.player.EntityPlayerMP;

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
	public PlayerStatsJS stats()
	{
		return new PlayerStatsJS(this, player.getStatFile());
	}

	public void kick(Text text)
	{
		player.connection.disconnect(text.component());
	}

	public void kick()
	{
		kick(new TextTranslate("multiplayer.disconnect.kicked", new Object[0]));
	}
}