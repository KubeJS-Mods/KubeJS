package dev.latvian.kubejs.script;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author LatvianModder
 */
public class ScriptModData
{
	public final String type;
	public final String mcVersion;
	public final HashSet<String> list;

	ScriptModData(String t, String mc, Collection<String> modList)
	{
		type = t;
		mcVersion = mc;
		list = new HashSet<>(modList);
	}

	public boolean isLoaded(String modId)
	{
		return list.contains(modId);
	}
}