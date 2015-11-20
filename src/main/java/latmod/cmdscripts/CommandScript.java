package latmod.cmdscripts;

import ftb.lib.cmd.*;
import latmod.lib.MathHelperLM;
import net.minecraft.command.*;
import net.minecraft.util.IChatComponent;

public class CommandScript extends CommandSubLM
{
	public CommandScript()
	{
		super("cmd_script", CommandLevel.OP);
		add(new CmdRun("run"));
		//add(new CmdTerminate("terminate"));
		add(new CmdTerminateAll("terminate_all"));
	}
	
	public static class CmdRun extends CommandLM
	{
		public CmdRun(String s)
		{ super(s, CommandLevel.OP); }
		
		public String[] getTabStrings(ICommandSender ics, String[] args, int i) throws CommandException
		{ return (i == 0) ? CmdScriptsEventHandler.files.keys.toStringArray() : new String[0]; }
		
		public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
		{
			checkArgs(args, 1);
			ScriptFile file = CmdScriptsEventHandler.files.get(args[0]);
			if(file == null) throw new ExceptionScriptNotFound(args[0]);
			CmdScriptsEventHandler.runScript(new ScriptInstance(MathHelperLM.rand.nextInt(), file, ics));
			return null;
		}
	}
	
	public static class CmdTerminate extends CommandLM
	{
		public CmdTerminate(String s)
		{ super(s, CommandLevel.OP); }
		
		public String[] getTabStrings(ICommandSender ics, String[] args, int i) throws CommandException
		{ return (i == 0) ? CmdScriptsEventHandler.running.toStringArray() : new String[0]; }
		
		public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
		{
			checkArgs(args, 1);
			ScriptFile file = CmdScriptsEventHandler.files.get(args[0]);
			if(file == null) throw new ExceptionScriptNotFound(args[0]);
			return null;
		}
	}
	
	public static class CmdTerminateAll extends CommandLM
	{
		public CmdTerminateAll(String s)
		{ super(s, CommandLevel.OP); }
		
		public String[] getTabStrings(ICommandSender ics, String[] args, int i) throws CommandException
		{ return (i == 0) ? CmdScriptsEventHandler.files.keys.toStringArray() : new String[0]; }
		
		public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
		{
			if(args.length == 1)
			{
				for(int i = 0; i < CmdScriptsEventHandler.running.size(); i++)
				{
					ScriptInstance inst = CmdScriptsEventHandler.running.get(i);
					if(inst.file.ID.equals(args[0])) inst.stop();
				}
			}
			else
			{
				for(int i = 0; i < CmdScriptsEventHandler.running.size(); i++)
					CmdScriptsEventHandler.running.get(i).stop();
			}
			
			return null;
		}
	}
}