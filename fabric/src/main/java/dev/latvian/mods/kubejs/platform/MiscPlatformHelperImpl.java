package dev.latvian.mods.kubejs.platform;

import dev.latvian.mods.kubejs.script.PlatformWrapper;
import net.fabricmc.loader.api.FabricLoader;

public class MiscPlatformHelperImpl implements MiscPlatformHelper {
	@Override
	public void setModName(PlatformWrapper.ModInfo info, String name) {
		try {
			var meta = FabricLoader.getInstance().getModContainer(info.getId()).get().getMetadata();
			var field = meta.getClass().getDeclaredField("name");
			field.setAccessible(true);
			field.set(meta, name);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
