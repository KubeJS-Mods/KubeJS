package dev.latvian.kubejs.player;

import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.text.TextTranslate;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * @author LatvianModder
 */
public class ServerPlayerJS extends PlayerJS<EntityPlayerMP>
{
	public ServerPlayerJS(PlayerDataJS d, WorldJS w, EntityPlayerMP p)
	{
		super(d, w, p);
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