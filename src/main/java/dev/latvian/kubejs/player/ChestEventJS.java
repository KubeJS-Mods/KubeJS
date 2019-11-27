package dev.latvian.kubejs.player;

import dev.latvian.kubejs.MinecraftClass;
import dev.latvian.kubejs.item.InventoryJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class ChestEventJS extends InventoryEventJS
{
	private InventoryJS inventory;

	public ChestEventJS(PlayerContainerEvent e)
	{
		super(e);
	}

	@MinecraftClass
	public IInventory getWrappedInventory()
	{
		return ((ChestContainer) getInventoryContainer()).getLowerChestInventory();
	}

	public InventoryJS getInventory()
	{
		if (inventory == null)
		{
			inventory = new InventoryJS(getWrappedInventory());
		}

		return inventory;
	}

	@Nullable
	public BlockContainerJS getBlock()
	{
		if (getWrappedInventory() instanceof TileEntity)
		{
			return getWorld().getBlock((TileEntity) getWrappedInventory());
		}

		return null;
	}
}