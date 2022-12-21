package dev.latvian.mods.kubejs.platform;

import dev.latvian.mods.kubejs.script.PlatformWrapper;
import dev.latvian.mods.kubejs.util.Lazy;
import net.minecraft.world.entity.MobCategory;

public interface MiscPlatformHelper {
	Lazy<MiscPlatformHelper> INSTANCE = Lazy.serviceLoader(MiscPlatformHelper.class);

	static MiscPlatformHelper get() {
		return INSTANCE.get();
	}

	void setModName(PlatformWrapper.ModInfo info, String name);

	MobCategory getMobCategory(String name);
}
