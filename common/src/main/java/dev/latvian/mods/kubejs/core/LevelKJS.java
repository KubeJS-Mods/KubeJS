package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.world.level.Level;

public interface LevelKJS extends AsKJS {
	@Override
	default Object asKJS() {
		return KubeJS.PROXY.getLevel((Level) this);
	}
}
