package dev.latvian.mods.kubejs.script;

import dev.architectury.injectables.targets.ArchitecturyTarget;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.SharedConstants;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author LatvianModder
 */
public class PlatformWrapper {
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

	private static final Set<String> MOD_LIST = new LinkedHashSet<>();
	private static final Map<String, ModInfo> MOD_MAP = new LinkedHashMap<>();

	static {
		for (var mod : Platform.getMods()) {
			ModInfo info = new ModInfo(mod.getModId());
			info.name = mod.getName();
			info.version = mod.getVersion();
			MOD_LIST.add(info.id);
			MOD_MAP.put(info.id, info);
		}
	}

	public static String getName() {
		return ArchitecturyTarget.getCurrentTarget();
	}

	public static boolean isForge() {
		return Platform.isForge();
	}

	public static boolean isFabric() {
		return Platform.isFabric();
	}

	public static String getMcVersion() {
		return SharedConstants.getCurrentVersion().getName();
	}

	public static Set<String> getList() {
		return MOD_LIST;
	}

	public static String getModVersion() {
		return getInfo(KubeJS.MOD_ID).version;
	}

	public static boolean isLoaded(String modId) {
		return MOD_MAP.containsKey(modId);
	}

	public static ModInfo getInfo(String modID) {
		return MOD_MAP.get(modID);
	}

	public static Map<String, ModInfo> getMods() {
		return MOD_MAP;
	}

	public static boolean isDevelopmentEnvironment() {
		return Platform.isDevelopmentEnvironment();
	}

	public static boolean isClientEnvironment() {
		return Platform.getEnvironment() == Env.CLIENT;
	}
}