package latmod.cmdscripts.cmd;

import ftb.lib.cmd.*;
import latmod.cmdscripts.PreUpdate;
import latmod.lib.*;
import net.minecraft.block.Block;
import net.minecraft.command.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;

public class CommandSignText extends CommandLM // CommandSetBlock
{
	public CommandSignText()
	{ super("signtext", CommandLevel.OP); }
	
	@SuppressWarnings("unchecked")
	public String[] getTabStrings(ICommandSender ics, String[] args, int i) throws CommandException
	{ return (i == 6) ? FastList.asList(Block.blockRegistry.getKeys()).toStringArray() : new String[0]; }
	
	public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
	{
		checkArgs(args, 5);
		
		ChunkCoordinates pos = ics.getPlayerCoordinates();
		int x = PreUpdate.parseRelInt(ics, pos.posX, args[0]);
		int y = PreUpdate.parseRelInt(ics, pos.posY, args[1]);
		int z = PreUpdate.parseRelInt(ics, pos.posZ, args[2]);
		
		TileEntity te = ics.getEntityWorld().getTileEntity(x, y, z);
		
		if(te != null && te instanceof TileEntitySign)
		{
			int l = parseIntBounded(ics, args[3], 1, 4) - 1;
			String text = LMStringUtils.unsplitSpaceUntilEnd(4, args);
			((TileEntitySign)te).signText[l] = text;
			//te.markDirty();
			te.getWorldObj().markBlockForUpdate(x, y, z);
		}
		
		return null;
	}
}