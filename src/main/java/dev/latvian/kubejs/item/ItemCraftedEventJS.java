package dev.latvian.kubejs.item;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import dev.latvian.kubejs.util.FieldJS;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class ItemCraftedEventJS extends PlayerEventJS
{
	public final PlayerEvent.ItemCraftedEvent event;
	private static final FieldJS posField = UtilsJS.getField(ContainerWorkbench.class, "pos", "field_178145_h");

	public ItemCraftedEventJS(PlayerEvent.ItemCraftedEvent e)
	{
		event = e;
	}

	@Override
	public EntityJS getEntity()
	{
		return entityOf(event.player);
	}

	public ItemStackJS getItem()
	{
		return ItemStackJS.of(event.crafting);
	}

	public InventoryJS getMatrix()
	{
		return new InventoryJS(event.craftMatrix);
	}

	@Nullable
	public BlockContainerJS getBlock()
	{
		if (event.player.openContainer instanceof ContainerWorkbench)
		{
			BlockPos pos = posField.get(event.player.openContainer);

			if (pos != null)
			{
				return new BlockContainerJS(event.player.world, pos);
			}
		}

		return null;
	}
}