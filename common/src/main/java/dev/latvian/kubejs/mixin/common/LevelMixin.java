package dev.latvian.kubejs.mixin.common;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.core.AsKJS;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Level.class)
public abstract class LevelMixin implements AsKJS {
	@Override
	public Object asKJS() {
		return KubeJS.PROXY.getWorld((Level) (Object) this);
	}
}
