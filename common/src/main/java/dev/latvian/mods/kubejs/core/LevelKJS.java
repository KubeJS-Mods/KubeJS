package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.level.LevelJS;
import net.minecraft.world.level.Level;

public interface LevelKJS extends AsKJS<LevelJS> {
	@Override
	default LevelJS asKJS() {
		return KubeJS.PROXY.getLevel((Level) this);
	}
}
