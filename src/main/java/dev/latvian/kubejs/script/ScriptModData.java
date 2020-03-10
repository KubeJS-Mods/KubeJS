package dev.latvian.kubejs.script;

import dev.latvian.kubejs.KubeJS;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author LatvianModder
 */
public class ScriptModData
{
	private static ScriptModData instance;

	public static ScriptModData getInstance()
	{
		if (instance == null)
		{
			instance = new ScriptModData("forge", "1.14.4", ModList.get().getMods());
		}

		return instance;
	}

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

	public ScriptModData(String t, String mc, List<net.minecraftforge.fml.loading.moddiscovery.ModInfo> modList)
	{
		type = t;
		mcVersion = mc;
		list = new HashSet<>(modList.size());

		for (net.minecraftforge.fml.loading.moddiscovery.ModInfo info : modList)
		{
			list.add(info.getModId());
		}
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

	public String getModVersion()
	{
		return ModList.get().getModContainerById(KubeJS.MOD_ID).get().getModInfo().getVersion().toString();
	}

	public boolean isLoaded(String modId)
	{
		return list.contains(modId);
	}

	public ModInfo getInfo(String modID)
	{
		ModInfo info = new ModInfo(modID);

		Optional<? extends ModContainer> modContainer = ModList.get().getModContainerById(modID);

		if (modContainer.isPresent())
		{
			IModInfo i = modContainer.get().getModInfo();
			info.name = i.getDisplayName();
			info.version = i.getVersion().toString();
		}

		return info;
	}
}