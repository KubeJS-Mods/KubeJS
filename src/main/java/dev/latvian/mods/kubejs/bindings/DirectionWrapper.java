package dev.latvian.mods.kubejs.bindings;

import net.minecraft.core.Direction;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface DirectionWrapper {
	Direction down = Direction.DOWN;
	Direction up = Direction.UP;
	Direction north = Direction.NORTH;
	Direction south = Direction.SOUTH;
	Direction west = Direction.WEST;
	Direction east = Direction.EAST;
	Direction DOWN = Direction.DOWN;
	Direction UP = Direction.UP;
	Direction NORTH = Direction.NORTH;
	Direction SOUTH = Direction.SOUTH;
	Direction WEST = Direction.WEST;
	Direction EAST = Direction.EAST;
	Direction[] VALUES = Direction.values();
	Map<String, Direction> ALL = Map.copyOf(Arrays.stream(VALUES).collect(Collectors.toMap(Direction::getSerializedName, Function.identity())));
	EnumSet<Direction> EMPTY_SET = EnumSet.noneOf(Direction.class);
}