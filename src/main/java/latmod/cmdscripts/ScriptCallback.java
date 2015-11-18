package latmod.cmdscripts;

import ftb.lib.FTBLib;
import ftb.lib.api.ServerTickCallback;
import ftb.lib.cmd.CommandLM;
import net.minecraft.command.ICommandSender;

public class ScriptCallback extends ServerTickCallback
{
	public final ScriptFile original;
	public final ScriptFile file;
	public final ICommandSender sender;
	
	public ScriptCallback(int i, ScriptFile o, ScriptFile f, ICommandSender s)
	{ super(i); original = o; file = f; sender = s; }
	
	public void onCallback()
	{
		if(file.commands.isEmpty() || sender == null || sender.getEntityWorld() == null) return;
		try
		{
			for(int i = 0; i < file.commands.size(); i++)
			{
				String s = file.commands.get(i);
				int line = i + (original.commands.size() - file.commands.size());
				
				if(s.isEmpty() || s.charAt(0) == '#') continue;
				else if(s.equals("exit"))
				{
					return;
				}
				else if(s.startsWith("goto "))
				{
					int line1 = CommandLM.parseInt(sender, s.split(" ")[1]);
					ScriptFile file1 = createSub(line1 + 1);
					FTBLib.addCallback(new ScriptCallback(0, original, file1, sender));
					return;
				}
				else if(s.startsWith("sleep ") || s.startsWith("delay "))
				{
					int delay = CommandLM.parseIntWithMin(sender, s.split(" ")[1], 1);
					ScriptFile file1 = createSub(line + 1);
					FTBLib.addCallback(new ScriptCallback(delay, original, file1, sender));
					return;
				}
				else
				{
					FTBLib.runCommand(sender, "/" + s);
				}
			}
		}
		catch(Exception e)
		{ e.printStackTrace(); }
	}
	
	public ScriptFile createSub(int line)
	{
		ScriptFile file = new ScriptFile(original.ID);
		file.commands.addAll(original.commands.subList(line, original.commands.size()));
		return file;
	}
}