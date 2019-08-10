package com.latmod.mods.kubejs.entity;

import com.latmod.mods.kubejs.text.TextUtils;
import com.latmod.mods.kubejs.world.WorldJS;
import net.minecraft.entity.Entity;

import java.util.Comparator;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class EntityJS
{
	public static final Comparator<? super EntityJS> COMPARATOR = (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName());

	public final WorldJS world;
	public final Entity entity;

	public EntityJS(WorldJS w, Entity e)
	{
		world = w;
		entity = e;
	}

	public UUID getID()
	{
		return entity.getUniqueID();
	}

	public String getName()
	{
		return entity.getName();
	}

	public void sendMessage(Object... message)
	{
		entity.sendMessage(TextUtils.INSTANCE.of(message).component());
	}

	public int hashCode()
	{
		return getID().hashCode();
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
		return getName();
	}

	public double getX()
	{
		return entity.posX;
	}

	public double getY()
	{
		return entity.posY;
	}

	public double getZ()
	{
		return entity.posZ;
	}

	public void setPosition(double x, double y, double z)
	{
		setPositionAndRotation(x, y, z, entity.rotationYaw, entity.rotationPitch);
	}

	public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch)
	{
		entity.setLocationAndAngles(x, y, z, yaw, pitch);
	}
}