package dev.latvian.kubejs.script;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ScriptPackInfo
{
	public final String namespace;
	public final ITextComponent displayName;
	public final List<ScriptFileInfo> scripts;
	public final String pathStart;

	public ScriptPackInfo(String n, Reader reader, String p)
	{
		JsonObject json = new JsonParser().parse(reader).getAsJsonObject();

		namespace = n;
		scripts = new ArrayList<>();
		pathStart = p;
		displayName = json.has("name") ? ITextComponent.Serializer.fromJson(json.get("name")) : new StringTextComponent(namespace);

		for (JsonElement e : json.get("scripts").getAsJsonArray())
		{
			scripts.add(new ScriptFileInfo(this, e.getAsJsonObject()));
		}
	}
}