package dev.latvian.kubejs.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import dev.latvian.kubejs.text.Text;
import jdk.nashorn.api.scripting.JSObject;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * @author LatvianModder
 */
public enum JsonUtilsJS
{
	INSTANCE;

	public JsonElement copy(@Nullable JsonElement element)
	{
		if (element == null || element.isJsonNull())
		{
			return JsonNull.INSTANCE;
		}
		else if (element instanceof JsonArray)
		{
			JsonArray a = new JsonArray();

			for (JsonElement e : (JsonArray) element)
			{
				a.add(copy(e));
			}

			return a;
		}
		else if (element instanceof JsonObject)
		{
			JsonObject o = new JsonObject();

			for (Map.Entry<String, JsonElement> entry : ((JsonObject) element).entrySet())
			{
				o.add(entry.getKey(), copy(entry.getValue()));
			}

			return o;
		}

		return element;
	}

	public JsonObject object()
	{
		return new JsonObject();
	}

	public JsonArray array()
	{
		return new JsonArray();
	}

	public JsonElement of(@Nullable Object object)
	{
		return of(object, 0, 5);
	}

	public JsonElement of(@Nullable Object object, int depth, int maxDepth)
	{
		if (maxDepth > 0 && depth > maxDepth)
		{
			return new JsonPrimitive("(depth > " + maxDepth + ")");
		}
		else if (object == null)
		{
			return JsonNull.INSTANCE;
		}
		else if (object instanceof JsonElement)
		{
			return (JsonElement) object;
		}
		else if (object instanceof String)
		{
			return new JsonPrimitive((String) object);
		}
		else if (object instanceof Boolean)
		{
			return new JsonPrimitive((Boolean) object);
		}
		else if (object instanceof Number)
		{
			return new JsonPrimitive((Number) object);
		}
		else if (object instanceof Character)
		{
			return new JsonPrimitive((Character) object);
		}
		else if (object instanceof Text)
		{
			return ((Text) object).json();
		}
		else if (object instanceof JSObject)
		{
			JSObject js = (JSObject) object;

			if (js.isArray())
			{
				JsonArray a = new JsonArray();

				for (String s : js.keySet())
				{
					a.add(of(js.getMember(s), depth, maxDepth));
				}

				return a;
			}
			else
			{
				JsonObject o = new JsonObject();

				for (String s : js.keySet())
				{
					o.add(s, of(js.getMember(s), depth, maxDepth));
				}

				return o;
			}
		}
		else if (object instanceof Map)
		{
			Map<?, ?> map = (Map<?, ?>) object;

			JsonObject o = new JsonObject();

			for (Map.Entry<?, ?> entry : map.entrySet())
			{
				o.add(String.valueOf(entry.getKey()), of(entry.getValue(), depth, maxDepth));
			}

			return o;
		}
		else if (object instanceof Iterable)
		{
			JsonArray a = new JsonArray();

			for (Object o : (Iterable) object)
			{
				a.add(of(o, depth, maxDepth));
			}

			return a;
		}

		JsonObject o = new JsonObject();

		try
		{
			Class c = object.getClass();
			o.addProperty("_", "<" + c.getName() + ">");

			for (Field field : c.getFields())
			{
				int m = field.getModifiers();

				if (Modifier.isPublic(m) && !Modifier.isStatic(m) && !Modifier.isTransient(m))
				{
					field.setAccessible(true);
					o.add(field.getName(), of(field.get(object), depth + 1, maxDepth));
				}
			}

			for (Method method : c.getMethods())
			{
				int m = method.getModifiers();

				if (Modifier.isPublic(m) && !Modifier.isStatic(m) && !UtilsJS.INSTANCE.internalMethods.contains(method.getName()))
				{
					method.setAccessible(true);
					o.addProperty(method.getName() + "()", "<" + method.getReturnType().getName() + ">");
				}
			}
		}
		catch (Throwable ex)
		{
			ex.printStackTrace();
		}

		return o;
	}

	public String toString(JsonElement json)
	{
		StringWriter writer = new StringWriter();

		try
		{
			JsonWriter jsonWriter = new JsonWriter(writer);
			jsonWriter.setSerializeNulls(true);
			jsonWriter.setLenient(true);
			Streams.write(json, jsonWriter);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}

		return writer.toString();
	}

	public String toPrettyString(JsonElement json)
	{
		StringWriter writer = new StringWriter();

		try
		{
			JsonWriter jsonWriter = new JsonWriter(writer);
			jsonWriter.setIndent("\t");
			jsonWriter.setSerializeNulls(true);
			jsonWriter.setLenient(true);
			Streams.write(json, jsonWriter);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}

		return writer.toString();
	}

	public JsonElement fromString(@Nullable String string)
	{
		if (string == null || string.isEmpty() || string.equals("null"))
		{
			return JsonNull.INSTANCE;
		}

		try
		{
			JsonReader jsonReader = new JsonReader(new StringReader(string));
			JsonElement element;
			boolean lenient = jsonReader.isLenient();
			jsonReader.setLenient(true);
			element = Streams.parse(jsonReader);

			if (!element.isJsonNull() && jsonReader.peek() != JsonToken.END_DOCUMENT)
			{
				throw new JsonSyntaxException("Did not consume the entire document.");
			}

			return element;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return JsonNull.INSTANCE;
	}

	@Nullable
	public Object primitiveObject(@Nullable JsonElement element)
	{
		if (element == null || element.isJsonNull())
		{
			return null;
		}
		else if (element.isJsonPrimitive())
		{
			JsonPrimitive p = element.getAsJsonPrimitive();

			if (p.isBoolean())
			{
				return p.getAsBoolean();
			}
			else if (p.isNumber())
			{
				return p.getAsNumber();
			}

			try
			{
				Double.parseDouble(p.getAsString());
				return p.getAsNumber();
			}
			catch (Exception ex)
			{
				return p.getAsString();
			}
		}

		return null;
	}
}