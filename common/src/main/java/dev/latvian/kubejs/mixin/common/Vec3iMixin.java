package dev.latvian.kubejs.mixin.common;

import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.core.Vec3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author LatvianModder
 */
@Mixin(Vec3i.class)
public abstract class Vec3iMixin {
	@Shadow
	@RemapForJS("getX")
	public abstract int getX();

	@Shadow
	@RemapForJS("getY")
	public abstract int getY();

	@Shadow
	@RemapForJS("getZ")
	public abstract int getZ();
}
