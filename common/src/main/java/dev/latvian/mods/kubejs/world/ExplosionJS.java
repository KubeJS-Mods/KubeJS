package dev.latvian.mods.kubejs.world;

import dev.latvian.mods.kubejs.entity.EntityJS;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

/**
 * @author LatvianModder
 */
public class ExplosionJS {
	private final LevelAccessor world;
	public final double x, y, z;
	public EntityJS exploder;
	public float strength;
	public boolean causesFire;
	public Explosion.BlockInteraction explosionMode;

	public ExplosionJS(LevelAccessor w, double _x, double _y, double _z) {
		world = w;
		x = _x;
		y = _y;
		z = _z;
		exploder = null;
		strength = 3F;
		causesFire = false;
		explosionMode = Explosion.BlockInteraction.BREAK;
	}

	public ExplosionJS exploder(EntityJS entity) {
		exploder = entity;
		return this;
	}

	public ExplosionJS strength(float f) {
		strength = f;
		return this;
	}

	public ExplosionJS causesFire(boolean b) {
		causesFire = b;
		return this;
	}

	public ExplosionJS damagesTerrain(boolean b) {
		explosionMode = b ? Explosion.BlockInteraction.BREAK : Explosion.BlockInteraction.NONE;
		return this;
	}

	public ExplosionJS destroysTerrain(boolean b) {
		explosionMode = b ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
		return this;
	}

	public void explode() {
		if (world instanceof Level) {
			((Level) world).explode(exploder == null ? null : exploder.minecraftEntity, x, y, z, strength, causesFire, explosionMode);
		}
	}
}