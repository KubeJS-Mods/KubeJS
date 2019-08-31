package dev.latvian.kubejs.player;

import dev.latvian.kubejs.script.AttachDataEvent;
import dev.latvian.kubejs.script.DataType;

import java.util.Map;

/**
 * @author LatvianModder
 */
public class AttachPlayerDataEvent extends AttachDataEvent<PlayerDataJS>
{
	public AttachPlayerDataEvent(PlayerDataJS p, Map<String, Object> d)
	{
		super(DataType.PLAYER, p, d);
	}
}