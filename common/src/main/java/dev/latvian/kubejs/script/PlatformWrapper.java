package dev.latvian.kubejs.script;

import dev.latvian.kubejs.KubeJS;
import me.shedaniel.architectury.platform.Mod;
import me.shedaniel.architectury.platform.Platform;
import me.shedaniel.architectury.utils.Env;
import net.minecraft.SharedConstants;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author LatvianModder
 */
public class PlatformWrapper {
	private static PlatformWrapper instance;

	public static PlatformWrapper getInstance() {
		if (instance == null) {
			instance = new PlatformWrapper();
		}

		return instance;
	}

	public static class ModInfo {
		private final String id;
		private String name;
		private String version;

		public ModInfo(String i) {
			id = i;
			name = id;
			version = "0.0.0";
		}

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public String getVersion() {
			return version;
		}
	}

	private final Set<String> list;
	private final Map<String, ModInfo> map;

	public PlatformWrapper() {
		map = new LinkedHashMap<>();

		for (Mod mod : Platform.getMods()) {
			ModInfo info = new ModInfo(mod.getModId());
			info.name = mod.getName();
			info.version = mod.getVersion();
			map.put(info.id, info);
		}

		list = map.keySet();
	}

	public String getName() {
		return Platform.getModLoader();
	}

	public boolean isForge() {
		return Platform.isForge();
	}

	public boolean isFabric() {
		return Platform.isFabric();
	}

	@Deprecated
	public String getType() {
		return Platform.getModLoader();
	}

	public String getMcVersion() {
		return SharedConstants.getCurrentVersion().getName();
	}

	public Set<String> getList() {
		return list;
	}

	public String getModVersion() {
		return getInfo(KubeJS.MOD_ID).version;
	}

	public boolean isLoaded(String modId) {
		return map.containsKey(modId);
	}

	public ModInfo getInfo(String modID) {
		return map.get(modID);
	}

	public Map<String, ModInfo> getMods() {
		return map;
	}

	public boolean isDevelopmentEnvironment() {
		return Platform.isDevelopmentEnvironment();
	}

	public boolean isClientEnvironment() {
		return Platform.getEnvironment() == Env.CLIENT;
	}
}