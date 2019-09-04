package dev.latvian.kubejs.player;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.text.Text;
import net.minecraftforge.event.ServerChatEvent;

/**
 * @author LatvianModder
 */
public class PlayerChatEventJS extends PlayerEventJS
{
	public final transient ServerChatEvent event;

	public PlayerChatEventJS(ServerChatEvent e)
	{
		event = e;
	}

	@Override
	public boolean canCancel()
	{
		return true;
	}

	@Override
	public EntityJS getEntity()
	{
		return entityOf(event.getPlayer());
	}

	public String getUsername()
	{
		return event.getUsername();
	}

	public String getMessage()
	{
		return event.getMessage();
	}

	public void setMessage(Text text)
	{
		event.setComponent(text.component());
	}
}