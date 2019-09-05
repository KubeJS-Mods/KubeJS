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
import jdk.nashorn.api.scripting.JSObject;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class JsonUtilsJS
{
	public static JsonElement copy(@Nullable JsonElement element)
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

	public static JsonElement of(@Nullable Object o)
	{
		if (o == null)
		{
			return JsonNull.INSTANCE;
		}
		else if (o instanceof JsonElement)
		{
			return (JsonElement) o;
		}
		else if (o instanceof CharSequence)
		{
			return new JsonPrimitive(o.toString());
		}
		else if (o instanceof Boolean)
		{
			return new JsonPrimitive((Boolean) o);
		}
		else if (o instanceof Number)
		{
			return new JsonPrimitive((Number) o);
		}
		else if (o instanceof Character)
		{
			return new JsonPrimitive((Character) o);
		}
		else if (o instanceof JsonSerializable)
		{
			return ((JsonSerializable) o).json();
		}
		else if (o instanceof JSObject)
		{
			JSObject js = (JSObject) o;

			if (js.isArray())
			{
				JsonArray json = new JsonArray();

				for (String s : js.keySet())
				{
					json.add(of(js.getMember(s)));
				}

				return json;
			}
			else
			{
				JsonObject json = new JsonObject();

				for (String s : js.keySet())
				{
					json.add(s, of(js.getMember(s)));
				}

				return json;
			}
		}
		else if (o instanceof Map)
		{
			Map<?, ?> map = (Map<?, ?>) o;

			JsonObject json = new JsonObject();

			for (Map.Entry<?, ?> entry : map.entrySet())
			{
				json.add(String.valueOf(entry.getKey()), of(entry.getValue()));
			}

			return json;
		}
		else if (o instanceof Iterable)
		{
			JsonArray a = new JsonArray();

			for (Object o1 : (Iterable) o)
			{
				a.add(of(o1));
			}

			return a;
		}

		return JsonNull.INSTANCE;
	}

	public static String toString(JsonElement json)
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

	public static String toPrettyString(JsonElement json)
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

	public static JsonElement fromString(@Nullable String string)
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
	public static Object primitiveObject(@Nullable JsonElement element)
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