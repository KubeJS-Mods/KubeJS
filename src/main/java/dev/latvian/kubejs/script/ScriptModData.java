package dev.latvian.kubejs.script;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author LatvianModder
 */
public class ScriptModData
{
	public static class ModInfo
	{
		public final String id;
		public String name;
		public String version;

		public ModInfo(String i)
		{
			id = i;
			name = id;
			version = "0.0.0";
		}
	}

	public final String type;
	public final String mcVersion;
	public final HashSet<String> list;

	public ScriptModData(String t, String mc, Collection<String> modList)
	{
		type = t;
		mcVersion = mc;
		list = new HashSet<>(modList);
	}

	public boolean isLoaded(String modId)
	{
		return list.contains(modId);
	}

	public ModInfo info(String modID)
	{
		ModInfo info = new ModInfo(modID);

		ModContainer modContainer = Loader.instance().getIndexedModList().get(modID);

		if (modContainer != null)
		{
			info.name = modContainer.getName();
			info.version = modContainer.getVersion();
		}

		return info;
	}
}