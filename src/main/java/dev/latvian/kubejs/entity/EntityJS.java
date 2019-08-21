package dev.latvian.kubejs.entity;

import dev.latvian.kubejs.text.TextUtils;
import dev.latvian.kubejs.util.ServerJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.entity.Entity;

import java.util.Comparator;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class EntityJS
{
	public static final Comparator<? super EntityJS> COMPARATOR = (o1, o2) -> o1.name().compareToIgnoreCase(o2.name());

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

	public UUID id()
	{
		return entity.getUniqueID();
	}

	public String name()
	{
		return entity.getName();
	}

	public void sendMessage(Object... message)
	{
		entity.sendMessage(TextUtils.INSTANCE.of(message).component());
	}

	public int hashCode()
	{
		return id().hashCode();
	}

	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}
		else if (o instanceof EntityJS)
		{
			EntityJS e = (EntityJS) o;
			return entity == e.entity || entity.getUniqueID().equals(e.entity.getUniqueID());
		}

		return false;
	}

	public String toString()
	{
		return name();
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

	public void runCommand(String command)
	{
		server.server.getCommandManager().executeCommand(entity, command);
	}
}