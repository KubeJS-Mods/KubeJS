package latmod.cmdscripts;

import ftb.lib.FTBLib;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.*;
import net.minecraft.world.World;

public final class ScriptSender implements ICommandSender
{
	public final ICommandSender parent;
	public final TileEntityCommandBlock commandBlock;
	public final EntityPlayerMP player;
	
	public String name;
	public World world;
	public ChunkCoordinates pos;
	
	public ScriptSender(ICommandSender s)
	{
		parent = s;
		name = s.getCommandSenderName();
		update();
		commandBlock = (s instanceof CommandBlockLogic) ? (TileEntityCommandBlock)world.getTileEntity(pos.posX, pos.posY, pos.posZ) : null;
		player = (s instanceof EntityPlayerMP) ? (EntityPlayerMP)s : null;
	}
	
	public boolean update()
	{
		if(parent == null) return false;
		else if(commandBlock != null && commandBlock.isInvalid()) return false;
		else if(player != null && !FTBLib.getServer().getConfigurationManager().playerEntityList.contains(player)) return false;
		world = parent.getEntityWorld();
		pos = parent.getPlayerCoordinates();
		return true;
	}
	
	public String getCommandSenderName()
	{ return name; }
	
	public IChatComponent func_145748_c_()
	{ return new ChatComponentText(name); }
	
	public void addChatMessage(IChatComponent c)
	{
	}
	
	public boolean canCommandSenderUseCommand(int i, String cmd)
	{ return true; }
	
	public ChunkCoordinates getPlayerCoordinates()
	{ return pos; }
	
	public World getEntityWorld()
	{ return world; }
}