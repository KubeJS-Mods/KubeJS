package dev.latvian.mods.kubejs.platform;

import dev.latvian.mods.kubejs.util.Lazy;
import net.minecraft.world.entity.MobCategory;

public interface MiscPlatformHelper {
	Lazy<MiscPlatformHelper> INSTANCE = Lazy.serviceLoader(MiscPlatformHelper.class);

	static MiscPlatformHelper get() {
		return INSTANCE.get();
	}

	MobCategory getMobCategory(String name);
}
