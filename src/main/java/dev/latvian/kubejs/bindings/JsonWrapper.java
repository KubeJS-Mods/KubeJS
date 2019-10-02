package dev.latvian.kubejs.bindings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.documentation.DisplayName;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.util.JsonUtilsJS;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
@DisplayName("JSONUtilities")
public class JsonWrapper
{
	public JsonNull getJsonNull()
	{
		return JsonNull.INSTANCE;
	}

	public JsonElement copy(@P("json") JsonElement json)
	{
		return JsonUtilsJS.copy(json);
	}

	public JsonObject object()
	{
		return new JsonObject();
	}

	public JsonArray array()
	{
		return new JsonArray();
	}

	public JsonElement of(@P("json") Object object)
	{
		return JsonUtilsJS.of(object);
	}

	public String toString(@P("json") JsonElement json)
	{
		return JsonUtilsJS.toString(json);
	}

	public String toPrettyString(@P("json") JsonElement json)
	{
		return JsonUtilsJS.toPrettyString(json);
	}

	public JsonElement fromString(@P("json") String json)
	{
		return JsonUtilsJS.fromString(json);
	}

	@Nullable
	public Object primitiveObject(@P("json") JsonElement json)
	{
		return JsonUtilsJS.primitiveObject(json);
	}
}