package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.core.ExplosionKJS;
import net.minecraft.world.level.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author LatvianModder
 */
@Mixin(Explosion.class)
public abstract class ExplosionMixin implements ExplosionKJS
{
	@Override
	@Accessor("size")
	public abstract float getSizeKJS();

	@Override
	@Accessor("size")
	public abstract void setSizeKJS(float size);
}