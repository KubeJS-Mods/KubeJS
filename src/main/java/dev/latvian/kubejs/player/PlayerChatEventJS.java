package dev.latvian.kubejs.player;

import dev.latvian.kubejs.text.Text;
import net.minecraftforge.event.ServerChatEvent;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class PlayerChatEventJS extends PlayerEventJS
{
	public final String username;
	public final String message;

	@Nullable
	public Text component;

	public PlayerChatEventJS(ServerChatEvent event)
	{
		super(event.getPlayer());
		username = event.getUsername();
		message = event.getMessage();
		component = null;
	}

	@Override
	public boolean canCancel()
	{
		return true;
	}
}