package dev.latvian.kubejs.player;

import dev.latvian.kubejs.docs.MinecraftClass;
import dev.latvian.kubejs.item.InventoryJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;

import org.jetbrains.annotations.Nullable;

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
	public Container getWrappedInventory()
	{
		return ((ChestMenu) getInventoryContainer()).getContainer();
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
		if (getWrappedInventory() instanceof BlockEntity)
		{
			return getWorld().getBlock((BlockEntity) getWrappedInventory());
		}

		return null;
	}
}