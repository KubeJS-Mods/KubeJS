package latmod.cmdscripts;

import java.io.File;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import ftb.lib.*;
import ftb.lib.api.*;
import ftb.lib.mod.FTBLibMod;
import latmod.lib.*;
import net.minecraft.command.*;
import net.minecraft.util.*;

public class CmdScriptsEventHandler
{
	public static final FastMap<String, ScriptFile> files = new FastMap<String, ScriptFile>();
	public static final FastList<ScriptInstance> running = new FastList<ScriptInstance>();
	private static final FastList<ScriptInstance> pending = new FastList<ScriptInstance>();
	
	@SubscribeEvent
	public void onReloaded(EventFTBReload e)
	{
		if(e.side.isServer() && e.sender != null)
		{
			files.clear();
			
			File folder = new File(e.sender.getEntityWorld().getSaveHandler().getWorldDirectory(), "/latmod/cmd_scripts/");
			if(!folder.exists()) folder.mkdirs();
			else
			{
				File[] f = folder.listFiles();
				
				for(File f1 : f)
				{
					if(f1.isFile() && f1.canRead() && f1.getName().endsWith(".script"))
					{
						try
						{
							FastList<String> l = LMFileUtils.load(f1);
							ScriptFile file = new ScriptFile(LMFileUtils.getRawFileName(f1));
							file.compile(l);
							files.put(file.ID, file);
						}
						catch(Exception ex)
						{ ex.printStackTrace(); }
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onWorldLoaded(EventFTBWorldServer e)
	{ reload(); }
	
	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent e) // FTBLibEventHandler
	{
		if(!e.world.isRemote && e.side == Side.SERVER && e.phase == TickEvent.Phase.END && e.type == TickEvent.Type.WORLD && e.world.provider.dimensionId == 0)
		{
			if(!pending.isEmpty())
			{
				running.addAll(pending);
				pending.clear();
			}
			
			if(!running.isEmpty())
			{
				for(int i = running.size() - 1; i >= 0; i--)
				{
					ScriptInstance inst = running.get(i);
					while(!inst.stopped())
					{
						try { inst.runCurrentLine(); }
						catch(Exception ex)
						{
							inst.stop();
							BroadcastSender.inst.addChatMessage(new ChatComponentText("Script '" + inst.file.ID + "' at " + LMStringUtils.stripI(inst.sender.pos.posX, inst.sender.pos.posY, inst.sender.pos.posZ) + " crashed at line " + (inst.currentLine() + 1) + ":"));
							
							if(ex instanceof CommandException)
							{
								CommandException cx = (CommandException)ex;
								BroadcastSender.inst.addChatMessage(new ChatComponentTranslation(cx.getMessage(), cx.getErrorOjbects()));
							}
							else
								BroadcastSender.inst.addChatMessage(new ChatComponentText(ex.toString()));
							ex.printStackTrace();
						}
						
						if(inst.isSleeping()) break;
					}
					if(inst.stopped()) running.remove(i);
				}
			}
		}
	}
	
	public static ScriptInstance runScript(ScriptFile file, ICommandSender sender, String[] args)
	{
		ScriptInstance inst = new ScriptInstance(MathHelperLM.rand.nextInt(), file, sender, args);
		pending.add(inst);
		return inst;
	}
	
	public static void reload()
	{
		pending.clear();
		running.clear();
		
		ScriptInstance.clearGlobalVariables(FTBLib.getServer());
		FTBLibMod.reload(FTBLib.getServer(), true, false);
		
		ScriptFile.startupFile = files.get("startup");
		ScriptFile.globalVariablesFile = files.get("global_variables");
		
		if(ScriptFile.startupFile != null) runScript(ScriptFile.startupFile, FTBLib.getServer(), new String[0]);
	}
}