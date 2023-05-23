package dev.latvian.mods.kubejs.platform.forge;

import dev.latvian.mods.kubejs.platform.MiscPlatformHelper;
import net.minecraft.world.entity.MobCategory;

public class MiscPlatformHelperImpl implements MiscPlatformHelper {
	@Override
	public MobCategory getMobCategory(String name) {
		return MobCategory.byName(name);
	}
}
