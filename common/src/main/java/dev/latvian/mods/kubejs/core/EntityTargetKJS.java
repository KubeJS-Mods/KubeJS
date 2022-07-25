package dev.latvian.mods.kubejs.core;

import net.minecraft.world.level.storage.loot.LootContext;

public interface EntityTargetKJS {
	String kjs$getName();

	@SuppressWarnings("ConstantConditions")
	static String kjs$getName(LootContext.EntityTarget target) {
		return ((EntityTargetKJS) (Object) target).kjs$getName();
	}
}
