package dev.latvian.kubejs.core;

import net.minecraft.nbt.Tag;

import java.util.Map;

/**
 * @author LatvianModder
 */
public interface CompoundNBTKJS
{
	Map<String, Tag> getTagsKJS();

	void setTagsKJS(Map<String, Tag> map);
}