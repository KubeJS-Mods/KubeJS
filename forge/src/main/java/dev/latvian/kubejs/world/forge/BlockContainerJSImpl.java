package dev.latvian.kubejs.world.forge;

import dev.latvian.kubejs.item.InventoryJS;
import dev.latvian.kubejs.item.ItemHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

public class BlockContainerJSImpl
{
	public static InventoryJS getInventoryFromBlockEntity(TileEntity tileEntity, Direction facing)
	{
		IItemHandler handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing).orElse(null);

		if (handler != null)
		{
			if (handler instanceof IItemHandlerModifiable)
			{
				return new InventoryJS(new ItemHandler.Mutable()
				{
					@Override
					public void setStackInSlot(int i, @Nonnull ItemStack itemStack)
					{
						((IItemHandlerModifiable) handler).setStackInSlot(i, itemStack);
					}

					@Override
					public int getSlots()
					{
						return handler.getSlots();
					}

					@Override
					public ItemStack getStackInSlot(int i)
					{
						return handler.getStackInSlot(i);
					}

					@Override
					public ItemStack insertItem(int i, ItemStack itemStack, boolean b)
					{
						return handler.insertItem(i, itemStack, b);
					}

					@Override
					public ItemStack extractItem(int i, int i1, boolean b)
					{
						return handler.extractItem(i, i1, b);
					}

					@Override
					public int getSlotLimit(int i)
					{
						return handler.getSlotLimit(i);
					}

					@Override
					public boolean isItemValid(int i, ItemStack itemStack)
					{
						return handler.isItemValid(i, itemStack);
					}
				});
			}
			return new InventoryJS(new ItemHandler()
			{
				@Override
				public int getSlots()
				{
					return handler.getSlots();
				}

				@Override
				public ItemStack getStackInSlot(int i)
				{
					return handler.getStackInSlot(i);
				}

				@Override
				public ItemStack insertItem(int i, ItemStack itemStack, boolean b)
				{
					return handler.insertItem(i, itemStack, b);
				}

				@Override
				public ItemStack extractItem(int i, int i1, boolean b)
				{
					return handler.extractItem(i, i1, b);
				}

				@Override
				public int getSlotLimit(int i)
				{
					return handler.getSlotLimit(i);
				}

				@Override
				public boolean isItemValid(int i, ItemStack itemStack)
				{
					return handler.isItemValid(i, itemStack);
				}
			});
		}

		return null;
	}
}
