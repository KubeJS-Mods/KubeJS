package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.kubejs.KubeJS;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.data.loading.DatagenModLoader;

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

			try {
				var mc = ModList.get().getModContainerById(id);

				if (mc.isPresent() && mc.get().getModInfo() instanceof net.neoforged.fml.loading.moddiscovery.ModInfo i) {
					var field = net.neoforged.fml.loading.moddiscovery.ModInfo.class.getDeclaredField("displayName");
					field.setAccessible(true);
					field.set(i, name);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
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
		return KubeJS.VERSION;
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

			for (var mod : ModList.get().getMods()) {
				var info = new ModInfo(mod.getModId());
				info.name = mod.getDisplayName();
				info.version = mod.getVersion().toString();
				allMods.put(info.id, info);
			}
		}

		return allMods;
	}

	public static boolean isDevelopmentEnvironment() {
		return !FMLLoader.isProduction();
	}

	public static boolean isClientEnvironment() {
		return FMLLoader.getDist() == Dist.CLIENT;
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
		return DatagenModLoader.isRunningDataGen();
	}

	public static void breakpoint(Object... args) {
		KubeJS.LOGGER.info(Arrays.stream(args).map(String::valueOf).collect(Collectors.joining(", ")));
	}
}