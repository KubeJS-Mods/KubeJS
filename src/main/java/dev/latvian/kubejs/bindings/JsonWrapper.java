package dev.latvian.kubejs.bindings;

import com.google.gson.JsonElement;
import dev.latvian.kubejs.documentation.DisplayName;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.documentation.T;
import dev.latvian.kubejs.util.JsonUtilsJS;
import dev.latvian.kubejs.util.MapJS;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;

/**
 * @author LatvianModder
 */
@DisplayName("JSONUtilities")
public class JsonWrapper
{
	public JsonElement copy(@P("json") JsonElement json)
	{
		return JsonUtilsJS.copy(json);
	}

	public String toString(@P("json") JsonElement json)
	{
		return JsonUtilsJS.toString(json);
	}

	public String toPrettyString(@P("json") JsonElement json)
	{
		return JsonUtilsJS.toPrettyString(json);
	}

	@Nullable
	public MapJS read(@P("file") File file) throws IOException
	{
		return JsonUtilsJS.read(file);
	}

	public void write(@P("file") File file, @P("json") @T(MapJS.class) Object json) throws IOException
	{
		JsonUtilsJS.write(file, MapJS.of(json));
	}

	@Nullable
	public MapJS read(@P("file") String file) throws IOException
	{
		return JsonUtilsJS.read(file);
	}

	public void write(@P("file") String file, @P("json") @T(MapJS.class) Object json) throws IOException
	{
		JsonUtilsJS.write(file, MapJS.of(json));
	}
}