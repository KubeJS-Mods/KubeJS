package dev.latvian.kubejs.mixin.common;

import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.core.Vec3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author LatvianModder
 */
@Mixin(Vec3i.class)
public abstract class Vec3iMixin {
	@Accessor("x")
	@RemapForJS("getX")
	public abstract int kjs_getX();

	@Accessor("y")
	@RemapForJS("getY")
	public abstract int kjs_getY();

	@Accessor("z")
	@RemapForJS("getZ")
	public abstract int kjs_getZ();
}
