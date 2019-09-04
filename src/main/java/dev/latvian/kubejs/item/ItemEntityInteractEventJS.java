package dev.latvian.kubejs.item;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/**
 * @author LatvianModder
 */
public class ItemEntityInteractEventJS extends PlayerEventJS
{
	public final transient Entity entity;
	public final boolean mainHand;

	public ItemEntityInteractEventJS(PlayerInteractEvent.EntityInteract event)
	{
		super(event.getEntityPlayer());
		entity = event.getTarget();
		mainHand = event.getHand() == EnumHand.MAIN_HAND;
	}

	public ItemStackJS item()
	{
		return player.inventory().getHandItem(mainHand);
	}

	public EntityJS entity()
	{
		return player.world.entity(entity);
	}

	@Override
	public boolean canCancel()
	{
		return true;
	}
}