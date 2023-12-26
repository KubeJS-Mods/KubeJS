package dev.latvian.mods.kubejs.script;

import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.helpers.MiscHelper;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PlatformWrapper {
	public static class ModInfo {
		private final String id;
		private String name;
		private String version;
		private String customName;

		public ModInfo(String i) {
			id = i;
			name = id;
			version = "0.0.0";
			customName = "";
		}

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public void setName(String n) {
			name = n;
			customName = n;
			MiscHelper.get().setModName(this, name);
		}

		public String getVersion() {
			return version;
		}

		public String getCustomName() {
			return customName;
		}
	}

	private static Map<String, ModInfo> allMods;

	@Deprecated
	public static String getName() {
		KubeJS.LOGGER.warn("Platform.getName() only exists for legacy reasons! If you have scripts that use this, please update them!");
		return "neoforge";
	}

	@Deprecated
	public static boolean isForge() {
		KubeJS.LOGGER.warn("Platform.isForge() only exists for legacy reasons! If you have scripts that use this, please update them!");
		return true;
	}

	@Deprecated
	public static boolean isFabric() {
		KubeJS.LOGGER.warn("Fabric support has been sunset; Platform.isFabric() will always return false!");
		return false;
	}

	public static String getMcVersion() {
		return KubeJS.MC_VERSION_STRING;
	}

	public static Set<String> getList() {
		return getMods().keySet();
	}

	public static String getModVersion() {
		return KubeJS.thisMod.getVersion();
	}

	public static boolean isLoaded(String modId) {
		return getMods().containsKey(modId);
	}

	public static ModInfo getInfo(String modID) {
		return getMods().computeIfAbsent(modID, ModInfo::new);
	}

	public static Map<String, ModInfo> getMods() {
		if (allMods == null) {
			allMods = new LinkedHashMap<>();

			for (var mod : Platform.getMods()) {
				var info = new ModInfo(mod.getModId());
				info.name = mod.getName();
				info.version = mod.getVersion();
				allMods.put(info.id, info);
			}
		}

		return allMods;
	}

	public static boolean isDevelopmentEnvironment() {
		return Platform.isDevelopmentEnvironment();
	}

	public static boolean isClientEnvironment() {
		return Platform.getEnvironment() == Env.CLIENT;
	}

	public static void setModName(String modId, String name) {
		getInfo(modId).setName(name);
	}

	public static int getMinecraftVersion() {
		return KubeJS.MC_VERSION_NUMBER;
	}

	public static String getMinecraftVersionString() {
		return KubeJS.MC_VERSION_STRING;
	}

	public static boolean isGeneratingData() {
		return MiscHelper.get().isDataGen();
	}

	public static void breakpoint(Object... args) {
		KubeJS.LOGGER.info(Arrays.stream(args).map(String::valueOf).collect(Collectors.joining(", ")));
	}
}