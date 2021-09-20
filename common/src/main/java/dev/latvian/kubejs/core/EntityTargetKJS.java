package dev.latvian.kubejs.core;

import net.minecraft.world.level.storage.loot.LootContext;

public interface EntityTargetKJS {
	String getNameKJS();

	@SuppressWarnings("ConstantConditions")
	static String getNameKJS(LootContext.EntityTarget target) {
		return ((EntityTargetKJS) (Object) target).getNameKJS();
	}
}
