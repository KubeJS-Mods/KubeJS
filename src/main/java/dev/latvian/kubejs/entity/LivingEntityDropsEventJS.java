package dev.latvian.kubejs.entity;

import dev.latvian.kubejs.item.ItemStackJS;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class LivingEntityDropsEventJS extends LivingEntityEventJS
{
	public final LivingDropsEvent event;
	public List<ItemEntityJS> drops;

	public LivingEntityDropsEventJS(LivingDropsEvent e)
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

	public DamageSourceJS getSource()
	{
		return new DamageSourceJS(getWorld(), event.getSource());
	}

	public int getLootingLevel()
	{
		return event.getLootingLevel();
	}

	public boolean isRecentlyHit()
	{
		return event.isRecentlyHit();
	}

	public List<ItemEntityJS> getDrops()
	{
		if (drops == null)
		{
			drops = new ArrayList<>();

			for (ItemEntity entity : event.getDrops())
			{
				drops.add(new ItemEntityJS(getWorld(), entity));
			}
		}

		return drops;
	}

	@Nullable
	public ItemEntityJS addDrop(Object item)
	{
		ItemStack i = ItemStackJS.of(item).getItemStack();

		if (!i.isEmpty())
		{
			Entity e = event.getEntity();
			ItemEntity ei = new ItemEntity(e.world, e.getPosX(), e.getPosY(), e.getPosZ(), i);
			ei.setPickupDelay(10);
			ItemEntityJS ie = new ItemEntityJS(getWorld(), ei);
			getDrops().add(ie);
			return ie;
		}

		return null;
	}

	@Nullable
	public ItemEntityJS addDrop(Object item, float chance)
	{
		if (chance >= 1F || event.getEntity().world.rand.nextFloat() <= chance)
		{
			return addDrop(item);
		}

		return null;
	}
}