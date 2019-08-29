package dev.latvian.kubejs.entity;

import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.text.TextUtilsJS;
import dev.latvian.kubejs.util.MessageSender;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;

import java.util.UUID;

/**
 * @author LatvianModder
 */
public class EntityJS implements MessageSender
{
	public final ServerJS server;
	public final transient Entity entity;

	public EntityJS(ServerJS s, Entity e)
	{
		server = s;
		entity = e;
	}

	public WorldJS world()
	{
		return server.world(entity.world);
	}

	public UUID uuid()
	{
		return entity.getUniqueID();
	}

	public String name()
	{
		return entity.getName();
	}

	public Text displayName()
	{
		return TextUtilsJS.INSTANCE.of(entity.getDisplayName());
	}

	@Override
	public void tell(Object message)
	{
		entity.sendMessage(TextUtilsJS.INSTANCE.of(message).component());
	}

	public String toString()
	{
		return name();
	}

	public ItemStackJS asItem()
	{
		if (entity instanceof EntityItem)
		{
			return new ItemStackJS.Bound(((EntityItem) entity).getItem());
		}

		return ItemStackJS.EMPTY;
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

	public float yaw()
	{
		return entity.rotationYaw;
	}

	public float pitch()
	{
		return entity.rotationPitch;
	}

	public void setPosition(double x, double y, double z)
	{
		setPositionAndRotation(x, y, z, yaw(), pitch());
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
		return server.server.getCommandManager().executeCommand(entity, command);
	}

	public void kill()
	{
		entity.onKillCommand();
	}
}