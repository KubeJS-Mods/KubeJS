package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.documentation.DisplayName;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.documentation.T;
import dev.latvian.kubejs.util.nbt.NBTBaseJS;
import dev.latvian.kubejs.util.nbt.NBTCompoundJS;
import dev.latvian.kubejs.util.nbt.NBTListJS;
import dev.latvian.kubejs.util.nbt.NBTNullJS;
import dev.latvian.kubejs.util.nbt.NBTStringJS;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;

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

	public NBTCompoundJS newCompound()
	{
		return new NBTCompoundJS();
	}

	public NBTListJS newList()
	{
		return new NBTListJS();
	}

	public Object read(@P("file") File file) throws IOException
	{
		return NBTCompoundJS.read(file);
	}

	public void write(@P("file") File file, @P("nbt") @T(NBTCompoundJS.class) Object nbt) throws IOException
	{
		NBTCompoundJS.write(file, nbt);
	}

	public Object read(@P("file") String file) throws IOException
	{
		return NBTCompoundJS.read(file);
	}

	public void write(@P("file") String file, @P("nbt") @T(NBTCompoundJS.class) Object nbt) throws IOException
	{
		NBTCompoundJS.write(file, nbt);
	}
}