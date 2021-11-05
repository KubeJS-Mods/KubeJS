package dev.latvian.kubejs.core;

import dev.latvian.kubejs.KubeJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

public interface EntityKJS extends AsKJS {
	@Override
	default Object asKJS() {
		Entity entity = (Entity) this;
		return KubeJS.PROXY.getWorld(entity.level).getEntity(entity);
	}

	CompoundTag getPersistentDataKJS();
}
