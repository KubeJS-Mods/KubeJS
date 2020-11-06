package dev.latvian.kubejs.player;

import dev.latvian.kubejs.docs.MinecraftClass;
import dev.latvian.kubejs.entity.EntityJS;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;

/**
 * @author LatvianModder
 */
public class InventoryEventJS extends PlayerEventJS
{
	private final PlayerContainerEvent event;

	public InventoryEventJS(PlayerContainerEvent e)
	{
		event = e;
	}

	@Override
	public EntityJS getEntity()
	{
		return entityOf(event);
	}

	@MinecraftClass
	public AbstractContainerMenu getInventoryContainer()
	{
		return event.getContainer();
	}
}