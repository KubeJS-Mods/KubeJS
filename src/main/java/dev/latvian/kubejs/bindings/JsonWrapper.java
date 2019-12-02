package dev.latvian.kubejs.bindings;

import com.google.gson.JsonElement;
import dev.latvian.kubejs.documentation.DisplayName;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.util.JsonUtilsJS;

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
	public Object read(File file) throws IOException
	{
		return JsonUtilsJS.read(file);
	}

	public void write(File file, Object json) throws IOException
	{
		JsonUtilsJS.write(file, json);
	}

	@Nullable
	public Object read(String file) throws IOException
	{
		return JsonUtilsJS.read(file);
	}

	public void write(String file, Object json) throws IOException
	{
		JsonUtilsJS.write(file, json);
	}
}