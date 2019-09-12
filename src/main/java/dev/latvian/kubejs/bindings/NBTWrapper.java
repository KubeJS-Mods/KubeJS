package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.documentation.DocClass;
import dev.latvian.kubejs.documentation.DocMethod;
import dev.latvian.kubejs.util.nbt.NBTBaseJS;
import dev.latvian.kubejs.util.nbt.NBTCompoundJS;
import dev.latvian.kubejs.util.nbt.NBTListJS;
import dev.latvian.kubejs.util.nbt.NBTNullJS;
import dev.latvian.kubejs.util.nbt.NBTStringJS;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
@DocClass(displayName = "NBT Utilities")
public class NBTWrapper
{
	@DocMethod
	public NBTNullJS getNullTag()
	{
		return NBTNullJS.INSTANCE;
	}

	@DocMethod
	public NBTCompoundJS getNullCompound()
	{
		return NBTNullJS.INSTANCE.asCompound();
	}

	@DocMethod
	public NBTListJS getNullList()
	{
		return NBTNullJS.INSTANCE.asList();
	}

	@DocMethod
	public NBTStringJS getEmptyString()
	{
		return NBTStringJS.EMPTY_STRING;
	}

	@DocMethod
	public NBTBaseJS of(@Nullable Object o)
	{
		return NBTBaseJS.of(o);
	}
}