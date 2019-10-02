package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.documentation.DisplayName;
import dev.latvian.kubejs.util.nbt.NBTBaseJS;
import dev.latvian.kubejs.util.nbt.NBTCompoundJS;
import dev.latvian.kubejs.util.nbt.NBTListJS;
import dev.latvian.kubejs.util.nbt.NBTNullJS;
import dev.latvian.kubejs.util.nbt.NBTStringJS;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
@DisplayName("NBTUtilities")
public class NBTWrapper
{
	public NBTNullJS getNullTag()
	{
		return NBTNullJS.INSTANCE;
	}

	public NBTCompoundJS getNullCompound()
	{
		return NBTNullJS.INSTANCE.asCompound();
	}

	public NBTListJS getNullList()
	{
		return NBTNullJS.INSTANCE.asList();
	}

	public NBTStringJS getEmptyString()
	{
		return NBTStringJS.EMPTY_STRING;
	}

	public NBTBaseJS of(@Nullable Object o)
	{
		return NBTBaseJS.of(o);
	}
}