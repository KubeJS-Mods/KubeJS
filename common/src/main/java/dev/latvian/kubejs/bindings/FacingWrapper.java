package dev.latvian.kubejs.bindings;

import net.minecraft.core.Direction;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author LatvianModder
 */
public class FacingWrapper {
	public static final Direction down = Direction.DOWN;
	public static final Direction up = Direction.UP;
	public static final Direction north = Direction.NORTH;
	public static final Direction south = Direction.SOUTH;
	public static final Direction west = Direction.WEST;
	public static final Direction east = Direction.EAST;
	public static final Map<String, Direction> map = Collections.unmodifiableMap(Arrays.stream(Direction.values()).collect(Collectors.toMap(Direction::getSerializedName, Function.identity())));

	public static Direction opposite(Direction facing) {
		return facing.getOpposite();
	}

	public static int x(Direction facing) {
		return facing.getStepX();
	}

	public static int y(Direction facing) {
		return facing.getStepY();
	}

	public static int z(Direction facing) {
		return facing.getStepZ();
	}

	public static int getIndex(Direction facing) {
		return facing.get3DDataValue();
	}

	public static int getHorizontalIndex(Direction facing) {
		return facing.get2DDataValue();
	}

	public static float getYaw(Direction facing) {
		return facing.toYRot();
	}

	public static float getPitch(Direction facing) {
		return facing == up ? 180F : facing == down ? 0F : 90F;
	}

	public static Direction rotateY(Direction facing) {
		return facing.getClockWise();
	}
}