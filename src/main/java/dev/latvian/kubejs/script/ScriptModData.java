package dev.latvian.kubejs.script;

import dev.latvian.kubejs.documentation.P;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author LatvianModder
 */
public class ScriptModData
{
	public static class ModInfo
	{
		private final String id;
		private String name;
		private String version;

		public ModInfo(String i)
		{
			id = i;
			name = id;
			version = "0.0.0";
		}

		public String getId()
		{
			return id;
		}

		public String getName()
		{
			return name;
		}

		public String getVersion()
		{
			return version;
		}
	}

	private final String type;
	private final String mcVersion;
	private final HashSet<String> list;

	public ScriptModData(String t, String mc, Collection<String> modList)
	{
		type = t;
		mcVersion = mc;
		list = new HashSet<>(modList);
	}

	public String getType()
	{
		return type;
	}

	public String getMcVersion()
	{
		return mcVersion;
	}

	public Set<String> getList()
	{
		return list;
	}

	public boolean isLoaded(@P("modID") String modId)
	{
		return list.contains(modId);
	}

	public ModInfo getInfo(@P("modID") String modID)
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