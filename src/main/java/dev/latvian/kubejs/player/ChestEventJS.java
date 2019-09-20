package dev.latvian.kubejs.player;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.item.InventoryJS;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.items.wrapper.InvWrapper;

/**
 * @author LatvianModder
 */
public class ChestEventJS extends PlayerEventJS
{
	private final EntityPlayer player;
	public final IInventory wrappedInventory;
	private InventoryJS inventory;

	public ChestEventJS(EntityPlayer p, IInventory inv)
	{
		player = p;
		wrappedInventory = inv;
	}

	@Override
	public boolean canCancel()
	{
		return true;
	}

	@Override
	public EntityJS getEntity()
	{
		return entityOf(player);
	}

	public InventoryJS getInventory()
	{
		if (inventory == null)
		{
			inventory = new InventoryJS(new InvWrapper(wrappedInventory));
		}

		return inventory;
	}
}