package dev.latvian.kubejs.script;

import com.google.common.collect.Sets;
import dev.latvian.kubejs.KubeJS;
import me.shedaniel.architectury.platform.Mod;
import me.shedaniel.architectury.platform.Platform;
import net.minecraft.SharedConstants;

import java.util.HashSet;
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
			instance = new ScriptModData(Platform.getModLoader(), SharedConstants.getCurrentVersion().getName());
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

	private final String modLoader;
	private final String mcVersion;
	private final HashSet<String> list;

	public ScriptModData(String modLoader, String mcVersion)
	{
		this.modLoader = modLoader;
		this.mcVersion = mcVersion;
		this.list = Sets.newHashSet();

		for (Mod mod : Platform.getMods())
		{
			this.list.add(mod.getModId());
		}
	}

	public String getType()
	{
		return modLoader;
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
		return Platform.getMod(KubeJS.MOD_ID).getVersion();
	}

	public boolean isLoaded(String modId)
	{
		return list.contains(modId);
	}

	public ModInfo getInfo(String modID)
	{
		ModInfo info = new ModInfo(modID);

		try
		{
			Mod mod = Platform.getMod(modID);

			info.name = mod.getName();
			info.version = mod.getVersion();
		}
		catch (Throwable ignored)
		{
		}

		return info;
	}
}