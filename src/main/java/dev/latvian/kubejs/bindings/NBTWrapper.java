package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.documentation.DisplayName;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.documentation.T;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.NBTUtilsJS;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;

/**
 * @author LatvianModder
 */
@DisplayName("NBTUtilities")
public class NBTWrapper
{
	@Nullable
	public MapJS read(@P("file") File file) throws IOException
	{
		return NBTUtilsJS.read(file);
	}

	public void write(@P("file") File file, @P("nbt") @T(MapJS.class) Object nbt) throws IOException
	{
		NBTUtilsJS.write(file, MapJS.of(nbt));
	}

	@Nullable
	public Object read(@P("file") String file) throws IOException
	{
		return NBTUtilsJS.read(file);
	}

	public void write(@P("file") String file, @P("nbt") @T(MapJS.class) Object nbt) throws IOException
	{
		NBTUtilsJS.write(file, MapJS.of(nbt));
	}
}