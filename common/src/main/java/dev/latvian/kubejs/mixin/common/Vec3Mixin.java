package dev.latvian.kubejs.mixin.common;

import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author LatvianModder
 */
@Mixin(Vec3.class)
public abstract class Vec3Mixin {
	@Shadow
	@Final
	@RemapForJS("x")
	public double x;

	@Shadow
	@Final
	@RemapForJS("y")
	public double y;

	@Shadow
	@Final
	@RemapForJS("z")
	public double z;
}
