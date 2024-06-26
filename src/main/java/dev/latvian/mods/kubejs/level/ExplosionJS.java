package dev.latvian.mods.kubejs.level;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class ExplosionJS {
	private final LevelAccessor level;
	public final double x, y, z;
	public Entity exploder;
	public float strength;
	public boolean causesFire;
	public Level.ExplosionInteraction explosionMode;

	public ExplosionJS(LevelAccessor level, double x, double y, double z) {
		this.level = level;
		this.x = x;
		this.y = y;
		this.z = z;
		this.exploder = null;
		this.strength = 3F;
		this.causesFire = false;
		this.explosionMode = Level.ExplosionInteraction.NONE;
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

	// TODO (low): change this
	public ExplosionJS explosionMode(Level.ExplosionInteraction mode) {
		explosionMode = mode;
		return this;
	}

	public void explode() {
		if (level instanceof Level level) {
			level.explode(exploder, x, y, z, strength, causesFire, explosionMode);
		}
	}
}