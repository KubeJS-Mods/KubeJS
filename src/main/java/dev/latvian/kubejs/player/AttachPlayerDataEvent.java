package dev.latvian.kubejs.player;

import dev.latvian.kubejs.script.AttachDataEvent;
import dev.latvian.kubejs.script.DataType;

/**
 * @author LatvianModder
 */
public class AttachPlayerDataEvent extends AttachDataEvent<PlayerDataJS>
{
	public AttachPlayerDataEvent(PlayerDataJS p)
	{
		super(DataType.PLAYER, p);
	}
}