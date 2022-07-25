package dev.latvian.mods.kubejs.level;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

/**
 * @author LatvianModder
 */
public class ExplosionJS {
	private final LevelAccessor level;
	public final double x, y, z;
	public Entity exploder;
	public float strength;
	public boolean causesFire;
	public Explosion.BlockInteraction explosionMode;

	public ExplosionJS(LevelAccessor l, double _x, double _y, double _z) {
		level = l;
		x = _x;
		y = _y;
		z = _z;
		exploder = null;
		strength = 3F;
		causesFire = false;
		explosionMode = Explosion.BlockInteraction.BREAK;
	}

	public ExplosionJS exploder(Entity entity) {
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
		if (level instanceof Level level) {
			level.explode(exploder, x, y, z, strength, causesFire, explosionMode);
		}
	}
}