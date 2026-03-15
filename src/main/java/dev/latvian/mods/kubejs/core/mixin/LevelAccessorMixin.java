package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LevelAccessor.class)
public interface LevelAccessorMixin {
	@Shadow
	@RemapForJS("getTime")
	long getGameTime();
}
