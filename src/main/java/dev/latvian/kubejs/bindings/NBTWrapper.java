package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.NBTUtilsJS;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;

/**
 * @author LatvianModder
 */
public class NBTWrapper
{
	@Nullable
	public MapJS read(File file) throws IOException
	{
		return NBTUtilsJS.read(file);
	}

	public void write(File file, Object nbt) throws IOException
	{
		NBTUtilsJS.write(file, MapJS.of(nbt));
	}

	@Nullable
	public Object read(String file) throws IOException
	{
		return NBTUtilsJS.read(file);
	}

	public void write(String file, Object nbt) throws IOException
	{
		NBTUtilsJS.write(file, MapJS.of(nbt));
	}
}