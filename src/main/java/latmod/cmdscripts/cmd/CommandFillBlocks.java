package latmod.cmdscripts.cmd;

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
		
		ChunkCoordinates pos = ics.getPlayerCoordinates();
		
		int x0 = parseRelInt(ics, pos.posX, args[0]);
		int y0 = parseRelInt(ics, pos.posY, args[1]);
		int z0 = parseRelInt(ics, pos.posZ, args[2]);
		
		int x1 = parseRelInt(ics, pos.posX, args[3]);
		int y1 = parseRelInt(ics, pos.posY, args[4]);
		int z1 = parseRelInt(ics, pos.posZ, args[5]);
		
		int minX = Math.min(x0, x1);
		int minY = Math.min(y0, y1);
		int minZ = Math.min(z0, z1);
		int maxX = Math.max(x0, x1);
		int maxY = Math.max(y0, y1);
		int maxZ = Math.max(z0, z1);
		
		Block block = getBlockByText(ics, args[6]);
		int meta = 0;
		if(args.length >= 8) meta = parseIntBounded(ics, args[7], 0, 15);
		World w = ics.getEntityWorld();
		
		for(int x = minX; x <= maxX; x++)
		{
			for(int z = minZ; z <= maxZ; z++)
			{
				for(int y = minY; y <= maxY; y++)
				{
					if(w.blockExists(x, y, z))
					{
						if(w.getBlock(x, y, z) != block || w.getBlockMetadata(x, y, z) != meta)
							w.setBlock(x, y, z, block, meta, 3);
					}
				}
			}
		}
		
		return null;
	}
}