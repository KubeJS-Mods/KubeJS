package dev.latvian.kubejs.world;

import dev.latvian.kubejs.entity.EntityJS;
import net.minecraft.world.World;

/**
 * @author LatvianModder
 */
public class ExplosionJS
{
	private final World world;
	public final double x, y, z;
	public EntityJS exploder;
	public float strength;
	public boolean causesFire;
	public boolean damagesTerrain;

	public ExplosionJS(World w, double _x, double _y, double _z)
	{
		world = w;
		x = _x;
		y = _y;
		z = _z;
		exploder = null;
		strength = 3F;
		causesFire = false;
		damagesTerrain = true;
	}

	public ExplosionJS exploder(EntityJS entity)
	{
		exploder = entity;
		return this;
	}

	public ExplosionJS strength(float f)
	{
		strength = f;
		return this;
	}

	public ExplosionJS causesFire(boolean b)
	{
		causesFire = b;
		return this;
	}

	public ExplosionJS damagesTerrain(boolean b)
	{
		damagesTerrain = b;
		return this;
	}

	public void explode()
	{
		world.newExplosion(exploder == null ? null : exploder.minecraftEntity, x, y, z, strength, causesFire, damagesTerrain);
	}
}