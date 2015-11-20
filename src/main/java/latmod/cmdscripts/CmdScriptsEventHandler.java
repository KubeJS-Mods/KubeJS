package latmod.cmdscripts;

import java.io.File;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import ftb.lib.FTBLib;
import ftb.lib.api.*;
import latmod.lib.*;

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
							ScriptFile file = new ScriptFile(f1.getName().replace(".script", "").trim());
							for(int i = 0; i < l.size(); i++) file.commands.add(l.get(i).trim());
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
	{
		ScriptFile file = files.get("startup");
		if(file != null) CmdScriptsEventHandler.runScript(new ScriptInstance(MathHelperLM.rand.nextInt(), file, FTBLib.getServer()));
	}
	
	public static void runScript(ScriptInstance inst)
	{ pending.add(inst); }
	
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
						inst.runCurrentLine();
						if(inst.isSleeping()) break;
					}
					if(inst.stopped()) running.remove(i);
				}
			}
		}
	}
}