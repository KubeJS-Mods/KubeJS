package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.documentation.DocClass;
import dev.latvian.kubejs.documentation.DocField;
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
	@DocField
	public final NBTNullJS nullTag = NBTNullJS.INSTANCE;

	@DocField
	public final NBTCompoundJS nullCompound = nullTag.asCompound();

	@DocField
	public final NBTListJS nullList = nullTag.asList();

	@DocField
	public NBTStringJS emptyString = NBTStringJS.EMPTY_STRING;

	@DocMethod
	public NBTBaseJS of(@Nullable Object o)
	{
		return NBTBaseJS.of(o);
	}
}