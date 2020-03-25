package dev.latvian.kubejs.core;

import net.minecraft.nbt.INBT;

import java.util.Map;

/**
 * @author LatvianModder
 */
public interface CompoundNBTKJS
{
	Map<String, INBT> getTagsKJS();

	void setTagsKJS(Map<String, INBT> map);
}