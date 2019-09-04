package dev.latvian.kubejs.entity;

import dev.latvian.kubejs.item.BoundItemStackJS;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.MessageSender;
import dev.latvian.kubejs.world.ServerWorldJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;

import java.util.UUID;

/**
 * @author LatvianModder
 */
public class EntityJS implements MessageSender
{
	public final WorldJS world;
	public final transient Entity entity;

	public EntityJS(WorldJS w, Entity e)
	{
		world = w;
		entity = e;
	}

	public boolean isServer()
	{
		return !entity.world.isRemote;
	}

	public UUID getID()
	{
		return entity.getUniqueID();
	}

	@Override
	public String getName()
	{
		return entity.getName();
	}

	@Override
	public Text getDisplayName()
	{
		return Text.of(entity.getDisplayName());
	}

	@Override
	public void tell(Object message)
	{
		entity.sendMessage(Text.of(message).component());
	}

	public String toString()
	{
		return getName() + "-" + getID();
	}

	public ItemStackJS asItem()
	{
		if (entity instanceof EntityItem)
		{
			return new BoundItemStackJS(((EntityItem) entity).getItem());
		}

		return EmptyItemStackJS.INSTANCE;
	}

	public boolean isLiving()
	{
		return false;
	}

	public boolean isPlayer()
	{
		return false;
	}

	public boolean isSneaking()
	{
		return entity.isSneaking();
	}

	public double x()
	{
		return entity.posX;
	}

	public double y()
	{
		return entity.posY;
	}

	public double z()
	{
		return entity.posZ;
	}

	public float getYaw()
	{
		return entity.rotationYaw;
	}

	public float getPitch()
	{
		return entity.rotationPitch;
	}

	public int getTicksExisted()
	{
		return entity.ticksExisted;
	}

	public void setPosition(double x, double y, double z)
	{
		setPositionAndRotation(x, y, z, getYaw(), getPitch());
	}

	public void setRotation(float yaw, float pitch)
	{
		setPositionAndRotation(x(), y(), z(), yaw, pitch);
	}

	public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch)
	{
		entity.setLocationAndAngles(x, y, z, yaw, pitch);
	}

	/*
	public void setDimensionPositionAndRotation(int dimension, double x, double y, double z, float yaw, float pitch)
	{
		setPositionAndRotation(x, y, z, yaw, pitch);
	}
	*/

	@Override
	public int runCommand(String command)
	{
		if (world instanceof ServerWorldJS)
		{
			return ((ServerWorldJS) world).server.server.getCommandManager().executeCommand(entity, command);
		}

		return 0;
	}

	public void kill()
	{
		entity.onKillCommand();
	}
}