package dev.latvian.kubejs.bindings;

import net.minecraft.util.EnumFacing;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class FacingWrapper
{
	public final EnumFacing down;
	public final EnumFacing up;
	public final EnumFacing north;
	public final EnumFacing south;
	public final EnumFacing west;
	public final EnumFacing east;
	public final Map<String, EnumFacing> map;

	public FacingWrapper()
	{
		down = EnumFacing.DOWN;
		up = EnumFacing.UP;
		north = EnumFacing.NORTH;
		south = EnumFacing.SOUTH;
		west = EnumFacing.WEST;
		east = EnumFacing.EAST;
		HashMap<String, EnumFacing> map0 = new HashMap<>();

		for (EnumFacing facing : EnumFacing.VALUES)
		{
			map0.put(facing.getName(), facing);
		}

		map = Collections.unmodifiableMap(map0);
	}

	public EnumFacing opposite(EnumFacing facing)
	{
		return facing.getOpposite();
	}

	public int x(EnumFacing facing)
	{
		return facing.getXOffset();
	}

	public int y(EnumFacing facing)
	{
		return facing.getYOffset();
	}

	public int z(EnumFacing facing)
	{
		return facing.getZOffset();
	}

	public int getIndex(EnumFacing facing)
	{
		return facing.getIndex();
	}

	public int getHorizontalIndex(EnumFacing facing)
	{
		return facing.getHorizontalIndex();
	}

	public float getYaw(EnumFacing facing)
	{
		return facing.getHorizontalAngle();
	}

	public float getPitch(EnumFacing facing)
	{
		return facing == up ? 180F : facing == down ? 0F : 90F;
	}

	public EnumFacing rotateY(EnumFacing facing)
	{
		return facing.rotateY();
	}
}