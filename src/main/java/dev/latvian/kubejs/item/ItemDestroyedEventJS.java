package dev.latvian.kubejs.item;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraft.util.Hand;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class ItemDestroyedEventJS extends PlayerEventJS
{
	public final PlayerDestroyItemEvent event;

	public ItemDestroyedEventJS(PlayerDestroyItemEvent e)
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

	@Nullable
	public Hand getHand()
	{
		return event.getHand();
	}

	public ItemStackJS getItem()
	{
		return ItemStackJS.of(event.getOriginal());
	}
}