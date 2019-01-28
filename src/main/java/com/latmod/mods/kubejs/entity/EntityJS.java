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
		return o == this || o instanceof EntityJS && getID().equals(((EntityJS) o).getID());
	}

	public String toString()
	{
		return getName();
	}
}