package dev.latvian.kubejs.player;

import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * @author LatvianModder
 */
public class PlayerDataCreatedEvent extends Event
{
	private final PlayerDataJS player;

	public PlayerDataCreatedEvent(PlayerDataJS p)
	{
		player = p;
	}

	public PlayerDataJS getPlayerData()
	{
		return player;
	}

	public void setData(String id, Object object)
	{
		player.data.put(id, object);
	}
}