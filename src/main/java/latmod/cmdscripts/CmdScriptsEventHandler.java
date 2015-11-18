package latmod.cmdscripts;

import java.io.File;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ftb.lib.api.EventFTBReload;
import latmod.lib.*;

public class CmdScriptsEventHandler
{
	public static final FastMap<String, ScriptFile> files = new FastMap<String, ScriptFile>();
	
	@SubscribeEvent
	public void onReloaded(EventFTBReload e)
	{
		if(e.side.isServer() && e.sender != null)
		{
			files.clear();
			
			File folder = new File(e.sender.getEntityWorld().getSaveHandler().getWorldDirectory(), "/latmod/scripts/");
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
							for(int i = 0; i < l.size(); i++)
								file.commands.add(l.get(i).trim());
							files.put(file.ID, file);
						}
						catch(Exception ex)
						{ ex.printStackTrace(); }
					}
				}
			}
		}
	}
}