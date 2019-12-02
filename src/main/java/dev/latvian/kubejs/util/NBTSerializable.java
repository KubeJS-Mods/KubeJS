package dev.latvian.kubejs.util;

import net.minecraft.nbt.INBT;

/**
 * @author LatvianModder
 */
public interface NBTSerializable
{
	INBT toNBT();
}