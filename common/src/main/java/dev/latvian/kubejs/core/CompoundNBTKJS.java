package dev.latvian.kubejs.core;

import java.util.Map;
import net.minecraft.nbt.Tag;

/**
 * @author LatvianModder
 */
public interface CompoundNBTKJS
{
	Map<String, Tag> getTagsKJS();

	void setTagsKJS(Map<String, Tag> map);
}