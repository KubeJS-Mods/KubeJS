package dev.latvian.kubejs.bindings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.documentation.DocClass;
import dev.latvian.kubejs.documentation.DocField;
import dev.latvian.kubejs.documentation.DocMethod;
import dev.latvian.kubejs.util.JsonUtilsJS;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
@DocClass(displayName = "JSON Utilities")
public class JsonWrapper
{
	@DocField
	public final JsonNull jsonNull = JsonNull.INSTANCE;

	@DocMethod
	public JsonElement copy(JsonElement element)
	{
		return JsonUtilsJS.copy(element);
	}

	@DocMethod
	public JsonObject object()
	{
		return new JsonObject();
	}

	@DocMethod
	public JsonArray array()
	{
		return new JsonArray();
	}

	@DocMethod
	public JsonElement of(Object object)
	{
		return JsonUtilsJS.of(object);
	}

	@DocMethod
	public String toString(JsonElement json)
	{
		return JsonUtilsJS.toString(json);
	}

	@DocMethod
	public String toPrettyString(JsonElement json)
	{
		return JsonUtilsJS.toPrettyString(json);
	}

	@DocMethod
	public JsonElement fromString(String string)
	{
		return JsonUtilsJS.fromString(string);
	}

	@Nullable
	@DocMethod
	public Object primitiveObject(JsonElement element)
	{
		return JsonUtilsJS.primitiveObject(element);
	}
}