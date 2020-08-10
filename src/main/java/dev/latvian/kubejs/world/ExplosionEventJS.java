package dev.latvian.kubejs.world;

import dev.latvian.kubejs.core.ExplosionKJS;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.entity.LivingEntityJS;
import dev.latvian.kubejs.player.EntityArrayList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.world.ExplosionEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public abstract class ExplosionEventJS extends WorldEventJS
{
	private final ExplosionEvent event;

	private ExplosionEventJS(ExplosionEvent e)
	{
		event = e;
	}

	@Override
	public WorldJS getWorld()
	{
		return worldOf(event.getWorld());
	}

	public Vec3d getPosition()
	{
		return event.getExplosion().getPosition();
	}

	public double getX()
	{
		return getPosition().x;
	}

	public double getY()
	{
		return getPosition().y;
	}

	public double getZ()
	{
		return getPosition().z;
	}

	public BlockContainerJS getBlock()
	{
		return new BlockContainerJS(event.getWorld(), new BlockPos(event.getExplosion().getPosition()));
	}

	@Nullable
	public LivingEntityJS getExploder()
	{
		return getWorld().getLivingEntity(event.getExplosion().getExplosivePlacedBy());
	}

	public static class Pre extends ExplosionEventJS
	{
		private final ExplosionEvent.Start event;

		public Pre(ExplosionEvent.Start e)
		{
			super(e);
			event = e;
		}

		@Override
		public boolean canCancel()
		{
			return true;
		}

		public float getSize()
		{
			return ((ExplosionKJS) event.getExplosion()).getSizeKJS();
		}

		public void setSize(float s)
		{
			((ExplosionKJS) event.getExplosion()).setSizeKJS(s);
		}
	}

	public static class Post extends ExplosionEventJS
	{
		private final ExplosionEvent.Detonate event;

		public Post(ExplosionEvent.Detonate e)
		{
			super(e);
			event = e;
		}

		public EntityArrayList getAffectedEntities()
		{
			return new EntityArrayList(getWorld(), event.getAffectedEntities());
		}

		public void removeAffectedEntity(EntityJS entity)
		{
			event.getAffectedEntities().remove(entity.minecraftEntity);
		}

		public void removeAllAffectedEntities()
		{
			event.getAffectedEntities().clear();
		}

		public List<BlockContainerJS> getAffectedBlocks()
		{
			List<BlockContainerJS> list = new ArrayList<>(event.getAffectedBlocks().size());

			for (BlockPos pos : event.getAffectedBlocks())
			{
				list.add(new BlockContainerJS(event.getWorld(), pos));
			}

			return list;
		}

		public void removeAffectedBlock(BlockContainerJS block)
		{
			event.getAffectedBlocks().remove(block.getPos());
		}

		public void removeAllAffectedBlocks()
		{
			event.getAffectedBlocks().clear();
		}

		public void removeKnockback()
		{
			event.getExplosion().getPlayerKnockbackMap().clear();
		}
	}
}