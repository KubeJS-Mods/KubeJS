package dev.latvian.kubejs.bindings;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.Direction;

/**
 * @author LatvianModder
 */
public class FacingWrapper
{
	public final Direction down;
	public final Direction up;
	public final Direction north;
	public final Direction south;
	public final Direction west;
	public final Direction east;
	public final Map<String, Direction> map;

	public FacingWrapper()
	{
		down = Direction.DOWN;
		up = Direction.UP;
		north = Direction.NORTH;
		south = Direction.SOUTH;
		west = Direction.WEST;
		east = Direction.EAST;
		HashMap<String, Direction> map0 = new HashMap<>();

		for (Direction facing : Direction.values())
		{
			map0.put(facing.getSerializedName(), facing);
		}

		map = Collections.unmodifiableMap(map0);
	}

	public Direction opposite(Direction facing)
	{
		return facing.getOpposite();
	}

	public int x(Direction facing)
	{
		return facing.getStepX();
	}

	public int y(Direction facing)
	{
		return facing.getStepY();
	}

	public int z(Direction facing)
	{
		return facing.getStepZ();
	}

	public int getIndex(Direction facing)
	{
		return facing.get3DDataValue();
	}

	public int getHorizontalIndex(Direction facing)
	{
		return facing.get2DDataValue();
	}

	public float getYaw(Direction facing)
	{
		return facing.toYRot();
	}

	public float getPitch(Direction facing)
	{
		return facing == up ? 180F : facing == down ? 0F : 90F;
	}

	public Direction rotateY(Direction facing)
	{
		return facing.getClockWise();
	}
}