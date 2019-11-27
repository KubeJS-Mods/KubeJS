package dev.latvian.kubejs.script;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;

/**
 * @author LatvianModder
 */
public class ScriptFileInfo
{
	public final ScriptPackInfo pack;
	public final String file;
	public final ResourceLocation location;

	public ScriptFileInfo(ScriptPackInfo p, JsonObject json)
	{
		pack = p;
		file = json.get("file").getAsString();
		location = new ResourceLocation(pack.namespace, pack.pathStart + file);
	}
}