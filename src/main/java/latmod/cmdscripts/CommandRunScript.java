package latmod.cmdscripts;

import ftb.lib.FTBLib;
import ftb.lib.cmd.*;
import net.minecraft.command.*;
import net.minecraft.util.IChatComponent;

public class CommandRunScript extends CommandLM
{
	public CommandRunScript()
	{ super("run_script", CommandLevel.OP); }
	
	public String[] getTabStrings(ICommandSender ics, String[] args, int i) throws CommandException
	{ return (i == 0) ? CmdScriptsEventHandler.files.keys.toStringArray() : new String[0]; }
	
	public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
	{
		checkArgs(args, 1);
		ScriptFile file = CmdScriptsEventHandler.files.get(args[0]);
		if(file == null) throw new ExceptionScriptNotFound(args[0]);
		FTBLib.addCallback(new ScriptCallback(0, file, file, ics));
		return null;
	}
}