package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

public interface EntityKJS extends AsKJS {
	@Override
	default Object asKJS() {
		var entity = (Entity) this;
		return KubeJS.PROXY.getLevel(entity.level).getEntity(entity);
	}

	CompoundTag getPersistentDataKJS();
}
