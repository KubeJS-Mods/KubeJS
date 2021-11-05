package dev.latvian.kubejs.core;

import dev.latvian.kubejs.KubeJS;
import net.minecraft.world.level.Level;

public interface LevelKJS extends AsKJS {
	@Override
	default Object asKJS() {
		return KubeJS.PROXY.getWorld((Level) this);
	}
}
