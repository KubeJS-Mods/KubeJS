package dev.latvian.kubejs.player;

import dev.latvian.kubejs.entity.EntityJS;
import net.minecraftforge.event.entity.player.AdvancementEvent;

/**
 * @author LatvianModder
 */
public class PlayerAdvancementEventJS extends PlayerEventJS
{
	public final AdvancementEvent event;

	public PlayerAdvancementEventJS(AdvancementEvent e)
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
		return entityOf(event);
	}

	public AdvancementJS getAdvancement()
	{
		return new AdvancementJS(event.getAdvancement());
	}
}