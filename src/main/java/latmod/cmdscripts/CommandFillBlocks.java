package latmod.cmdscripts;

import ftb.lib.cmd.*;
import latmod.lib.FastList;
import net.minecraft.block.Block;
import net.minecraft.command.*;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class CommandFillBlocks extends CommandLM // CommandSetBlock
{
	public CommandFillBlocks()
	{ super("fillblock", CommandLevel.OP); }
	
	@SuppressWarnings("unchecked")
	public String[] getTabStrings(ICommandSender ics, String[] args, int i) throws CommandException
	{ return (i == 6) ? FastList.asList(Block.blockRegistry.getKeys()).toStringArray() : new String[0]; }
	
	public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
	{
		checkArgs(args, 7);
		
		int x0 = MathHelper.floor_double(func_110666_a(ics, ics.getPlayerCoordinates().posX, args[0]));
		int y0 = MathHelper.floor_double(func_110666_a(ics, ics.getPlayerCoordinates().posY, args[1]));
		int z0 = MathHelper.floor_double(func_110666_a(ics, ics.getPlayerCoordinates().posZ, args[2]));
		
		int x1 = MathHelper.floor_double(func_110666_a(ics, ics.getPlayerCoordinates().posX, args[3]));
		int y1 = MathHelper.floor_double(func_110666_a(ics, ics.getPlayerCoordinates().posY, args[4]));
		int z1 = MathHelper.floor_double(func_110666_a(ics, ics.getPlayerCoordinates().posZ, args[5]));
		
		Block block = getBlockByText(ics, args[6]);
		int l = 0;
		if(args.length >= 8) l = parseIntBounded(ics, args[7], 0, 15);
		World w = ics.getEntityWorld();
		
		int minX = Math.min(x0, x1);
		int minY = Math.min(y0, y1);
		int minZ = Math.min(z0, z1);
		int maxX = Math.max(x0, x1);
		int maxY = Math.max(y0, y1);
		int maxZ = Math.max(z0, z1);
		
		for(int x = minX; x < maxX; x++) for(int z = minZ; z < maxZ; z++)
		{
			if(w.blockExists(x, 0, z))
			{
				for(int y = minY; y < maxY; y++)
				{
					Block block0 = w.getBlock(x, y, z);
					
					if(block0 != block || w.getBlockMetadata(x, y, z) != l)
						w.setBlock(x, y, z, block, l, 3);
				}
			}
		}
		
		return null;
	}
}