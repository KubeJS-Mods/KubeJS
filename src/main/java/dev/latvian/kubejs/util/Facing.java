package dev.latvian.kubejs.util;

import net.minecraft.util.EnumFacing;

/**
 * @author LatvianModder
 */
public enum Facing
{
	DOWN("down", 0, 1, -1, EnumFacing.DOWN),
	UP("up", 1, 0, -1, EnumFacing.UP),
	NORTH("north", 2, 3, 2, EnumFacing.NORTH),
	SOUTH("south", 3, 2, 0, EnumFacing.SOUTH),
	WEST("west", 4, 5, 1, EnumFacing.WEST),
	EAST("east", 5, 4, 3, EnumFacing.EAST);

	public static final Facing[] VALUES = values();

	public final String name;
	public final int index;
	public final int oppositeIndex;
	public final int horizontalIndex;
	public final EnumFacing vanillaFacing;
	public Facing opposite;

	Facing(String n, int i, int o, int h, EnumFacing f)
	{
		name = n;
		index = i;
		oppositeIndex = o;
		horizontalIndex = h;
		vanillaFacing = f;
	}
}