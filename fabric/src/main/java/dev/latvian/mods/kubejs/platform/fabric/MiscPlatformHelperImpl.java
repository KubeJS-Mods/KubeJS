package dev.latvian.mods.kubejs.platform.fabric;

import dev.latvian.mods.kubejs.platform.MiscPlatformHelper;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.MobCategory;

public class MiscPlatformHelperImpl implements MiscPlatformHelper {
	@Override
	public MobCategory getMobCategory(String name) {
		// safe cast, mojang just specified too general of a type
		return ((StringRepresentable.EnumCodec<MobCategory>) MobCategory.CODEC).byName(name);
	}
}
