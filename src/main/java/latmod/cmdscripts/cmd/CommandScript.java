package latmod.cmdscripts.cmd;

import java.io.File;
import java.net.URL;

import ftb.lib.cmd.*;
import latmod.cmdscripts.*;
import latmod.lib.*;
import net.minecraft.command.*;
import net.minecraft.util.*;

public class CommandScript extends CommandSubLM
{
	public CommandScript()
	{
		super("cmd_script", CommandLevel.OP);
		add(new CmdRun("run"));
		//add(new CmdTerminate("terminate"));
		add(new CmdTerminateAll("terminate_all"));
		add(new CmdClearGlobal("clear_global_variables"));
		add(new CmdDownload("download"));
		add(new CmdDelete("delete"));
		add(new CmdReload("reload"));
	}
	
	public static class CmdRun extends CommandLM
	{
		public CmdRun(String s)
		{ super(s, CommandLevel.OP); }
		
		public String getCommandUsage(ICommandSender ics)
		{ return '/' + commandName + " <ID>"; }
		
		public String[] getTabStrings(ICommandSender ics, String[] args, int i) throws CommandException
		{ return (i == 0) ? LMStringUtils.toStringArray(CmdScriptsEventHandler.files.keys) : new String[0]; }
		
		public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
		{
			checkArgs(args, 1);
			ScriptFile file = CmdScriptsEventHandler.files.get(args[0]);
			if(file == null) throw new CommandException("command.cmdscripts.not_found", args[0]);
			CmdScriptsEventHandler.runScript(file, ics, LMStringUtils.shiftArray(args));
			return null;
		}
	}
	
	public static class CmdTerminate extends CommandLM
	{
		public CmdTerminate(String s)
		{ super(s, CommandLevel.OP); }
		
		public String getCommandUsage(ICommandSender ics)
		{ return '/' + commandName + " <ID>"; }
		
		public String[] getTabStrings(ICommandSender ics, String[] args, int i) throws CommandException
		{ return (i == 0) ? CmdScriptsEventHandler.running.toStringArray() : new String[0]; }
		
		public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
		{
			checkArgs(args, 1);
			ScriptInstance inst = CmdScriptsEventHandler.running.getObj(args[0]);
			if(inst != null) inst.stop();
			return null;
		}
	}
	
	public static class CmdTerminateAll extends CommandLM
	{
		public CmdTerminateAll(String s)
		{ super(s, CommandLevel.OP); }
		
		public String getCommandUsage(ICommandSender ics)
		{ return '/' + commandName + " [ID]"; }
		
		public String[] getTabStrings(ICommandSender ics, String[] args, int i) throws CommandException
		{ return (i == 0) ? LMStringUtils.toStringArray(CmdScriptsEventHandler.files.keys) : new String[0]; }
		
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
	
	public static class CmdDownload extends CommandLM
	{
		public CmdDownload(String s)
		{ super(s, CommandLevel.OP); }
		
		public String getCommandUsage(ICommandSender ics)
		{ return '/' + commandName + " <raw text link> <ID>"; }
		
		public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
		{
			checkArgs(args, 2);
			
			try
			{
				File f = LMFileUtils.newFile(new File(ics.getEntityWorld().getSaveHandler().getWorldDirectory(), "/latmod/cmd_scripts/" + args[1] + ".script"));
				FastList<String> list = LMStringUtils.readStringList(new URL(args[0]).openStream());
				LMFileUtils.save(f, list);
				return new ChatComponentText("Script downloaded!");
			}
			catch(Exception e)
			{ e.printStackTrace(); }
			
			return error(new ChatComponentText("Download failed!"));
		}
	}
	
	public static class CmdDelete extends CommandLM
	{
		public CmdDelete(String s)
		{ super(s, CommandLevel.OP); }
		
		public String[] getTabStrings(ICommandSender ics, String[] args, int i) throws CommandException
		{ return (i == 0) ? LMStringUtils.toStringArray(CmdScriptsEventHandler.files.keys) : new String[0]; }
		
		public String getCommandUsage(ICommandSender ics)
		{ return '/' + commandName + " <ID>"; }
		
		public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
		{
			checkArgs(args, 1);
			
			File f;
			if(args[0].equals("*")) f = new File(ics.getEntityWorld().getSaveHandler().getWorldDirectory(), "/latmod/cmd_scripts/");
			else f = new File(ics.getEntityWorld().getSaveHandler().getWorldDirectory(), "/latmod/cmd_scripts/" + args[0] + ".script");
			if(LMFileUtils.delete(f)) return new ChatComponentText("Script deleted!");
			return error(new ChatComponentText("Error!"));
		}
	}
	
	public static class CmdClearGlobal extends CommandLM
	{
		public CmdClearGlobal(String s)
		{ super(s, CommandLevel.OP); }
		
		public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
		{ ScriptInstance.clearGlobalVariables(ics); return null; }
	}
	
	public static class CmdReload extends CommandLM
	{
		public CmdReload(String s)
		{ super(s, CommandLevel.OP); }
		
		public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
		{ CmdScriptsEventHandler.reload(ics); return null; }
	}
}