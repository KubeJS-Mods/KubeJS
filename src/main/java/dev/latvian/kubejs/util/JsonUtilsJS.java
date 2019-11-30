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
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.documentation.P;

import javax.annotation.Nullable;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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
		else if (o instanceof JsonSerializable)
		{
			return ((JsonSerializable) o).getJson();
		}

		return ofNormalized(UtilsJS.normalize(o));
	}

	private static JsonElement ofNormalized(@Nullable Object o)
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
		else if (o instanceof Map)
		{
			JsonObject json = new JsonObject();

			for (Map.Entry entry : ((Map<?, ?>) o).entrySet())
			{
				json.add(entry.getKey().toString(), ofNormalized(entry.getValue()));
			}

			return json;
		}
		else if (o instanceof Iterable)
		{
			JsonArray json = new JsonArray();

			for (Object o1 : (Iterable) o)
			{
				json.add(ofNormalized(o1));
			}

			return json;
		}

		return new JsonPrimitive("<" + o.getClass().getName() + ":" + o + ">");
	}

	@Nullable
	public static Object toObject(@Nullable JsonElement json)
	{
		if (json == null || json.isJsonNull())
		{
			return null;
		}
		else if (json.isJsonObject())
		{
			LinkedHashMap<String, Object> map = new LinkedHashMap<>();
			JsonObject o = json.getAsJsonObject();

			for (Map.Entry<String, JsonElement> entry : o.entrySet())
			{
				map.put(entry.getKey(), toObject(entry.getValue()));
			}

			return map;
		}
		else if (json.isJsonArray())
		{
			JsonArray a = json.getAsJsonArray();
			List<Object> objects = new ArrayList<>(a.size());

			for (JsonElement e : a)
			{
				objects.add(toObject(e));
			}

			return objects;
		}

		return primitiveObject(json);
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

	@Nullable
	public static Object read(@P("file") File file) throws IOException
	{
		KubeJS.verifyFilePath(file);

		try (FileReader fileReader = new FileReader(file);
			 JsonReader jsonReader = new JsonReader(fileReader))
		{
			JsonElement element;
			boolean lenient = jsonReader.isLenient();
			jsonReader.setLenient(true);
			element = Streams.parse(jsonReader);

			if (!element.isJsonNull() && jsonReader.peek() != JsonToken.END_DOCUMENT)
			{
				throw new JsonSyntaxException("Did not consume the entire document.");
			}

			return toObject(element);
		}
	}

	public static void write(@P("file") File file, @P("json") Object json) throws IOException
	{
		KubeJS.verifyFilePath(file);

		String string = JsonUtilsJS.toPrettyString(JsonUtilsJS.of(json));

		try (Writer fileWriter = new FileWriter(file);
			 JsonWriter jsonWriter = new JsonWriter(new BufferedWriter(fileWriter)))
		{
			jsonWriter.setIndent("\t");
			jsonWriter.setSerializeNulls(true);
			jsonWriter.setLenient(true);
			Streams.write(of(json), jsonWriter);
		}
	}

	@Nullable
	public static Object read(@P("file") String file) throws IOException
	{
		return read(KubeJS.getGameDirectory().resolve(file).toFile());
	}

	public static void write(@P("file") String file, @P("json") Object json) throws IOException
	{
		write(KubeJS.getGameDirectory().resolve(file).toFile(), json);
	}
}