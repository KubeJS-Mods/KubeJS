package latmod.cmdscripts;

import ftb.lib.FTBLib;
import ftb.lib.cmd.CommandLM;
import latmod.lib.*;
import net.minecraft.block.Block;
import net.minecraft.command.*;

public class ScriptInstance
{
	public final int ID;
	public final ScriptFile file;
	public final ScriptSender sender;
	public final FastList<String> lines;
	public final FastMap<String, Integer> variables;
	private static final FastMap<String, Integer> globalVariables = new FastMap<String, Integer>();
	private final IntList lastGotoLine;
	
	private int currentLine = 0;
	private int sleepTimer = 0;
	private boolean stopped = false;
	
	public ScriptInstance(int id, ScriptFile f, ICommandSender s, String[] args)
	{
		ID = id;
		file = f;
		sender = new ScriptSender(s);
		lines = new FastList<String>();
		variables = new FastMap<String, Integer>();
		lastGotoLine = new IntList();
		lastGotoLine.add(0);
		
		if(args.length > 0)
		{
			for(int j = 0; j < args.length; j++)
				args[j] = formatSpaces(args[j]);
			
			for(int i = 0; i < f.lines.size(); i++)
			{
				if(!f.ignored.contains(i))
				{
					String s1 = f.lines.get(i);
					
					for(int j = 0; j < args.length; j++)
						s1 = s1.replace("$arg_" + (j + 1), args[j]);
					
					lines.add(s1);
				}
				else lines.add("");
			}
		}
		else lines.addAll(f.lines);
	}
	
	private static String formatSpaces(String s)
	{
		if(s == null) return null;
		else if(s.isEmpty()) return s;
		else return s.replace("\\_", " ");
	}
	
	public int hashCode()
	{ return ID; }
	
	public boolean equals(Object o)
	{ return o != null && (o == this || ((o instanceof CharSequence) ? toString().equals(o.toString()) : (hashCode() == o.hashCode()))); }
	
	public String toString()
	{ return Integer.toHexString(ID); }
	
	public void runCurrentLine()
	{
		if(lines.isEmpty() || !sender.update()) { stop(); return; }
		else if(sleepTimer > 0) { sleepTimer--; return; }
		
		String s = lines.get(currentLine);
		
		if(s == null) { stop(); return; }
		
		while(file.ignored.contains(currentLine))
		{
			currentLine++;
			s = lines.get(currentLine);
			if(s == null) { stop(); return; }
		}
		
		for(int i = 0; i < variables.size(); i++)
			s = s.replace('$' + variables.keys.get(i), Integer.toString(variables.values.get(i)));
		
		String[] cmd = (s.indexOf(' ') == -1) ? new String[] { s } : s.split(" ");
		
		if(cmd.length > 0)
		{
			if(cmd[0].equals("exit")) { stop(); return; }
			else if(cmd[0].equals("setName"))
			{
				CommandLM.checkArgs(cmd, 2);
				sender.name = LMStringUtils.unsplitSpaceUntilEnd(1, cmd);
			}
			else if(cmd[0].equals("goto"))
			{
				CommandLM.checkArgs(cmd, 2);
				gotoFunc(cmd[1]);
				return;
			}
			else if(cmd[0].equals("return"))
			{
				int siz = lastGotoLine.size();
				if(siz > 0)
				{
					currentLine = lastGotoLine.get(siz - 1) + 1;
					lastGotoLine.removeKey(siz - 1);
					return;
				}
				else throw new IndexOutOfBoundsException("Can't return from here");
			}
			else if(cmd[0].equals("sleep"))
			{
				CommandLM.checkArgs(cmd, 2);
				sleepTimer = CommandLM.parseIntWithMin(sender, cmd[1], 1);
			}
			else if(cmd[0].equals("setVar"))
			{
				CommandLM.checkArgs(cmd, 3);
				
				Integer i0 = variables.get(cmd[1]);
				int i = CommandLM.parseRelInt(sender, (i0 == null) ? 0 : i0.intValue(), cmd[2]);
				variables.put(cmd[1], Integer.valueOf(i));
			}
			else if(cmd[0].equals("ifVar"))
			{
				CommandLM.checkArgs(cmd, 5);
				
				Integer a = variables.get(cmd[1]);
				if(a == null) throw new CommandException("command.cmdscripts.unknown_var", cmd[1]);
				
				int b = CommandLM.parseInt(sender, cmd[3]);
				
				byte comp = NumberComparator.getComparator(cmd[2]);
				if(NumberComparator.compare(a.intValue(), b, comp))
				{ gotoFunc(cmd[4]); return; }
				else if(cmd.length >= 6)
				{ gotoFunc(cmd[5]); return; }
			}
			else if(cmd[0].equals("setGlobalVar"))
			{
				CommandLM.checkArgs(cmd, 3);
				Integer i0 = globalVariables.get(cmd[1]);
				int ii0 = (i0 == null) ? 0 : i0.intValue();
				int i = CommandLM.parseRelInt(sender, ii0, cmd[2]);
				setGlobalVariable(sender, cmd[1], i);
			}
			else if(cmd[0].equals("ifGlobalVar"))
			{
				CommandLM.checkArgs(cmd, 5);
				
				Integer var = globalVariables.get(cmd[1]);
				if(var == null) throw new CommandException("command.cmdscripts.unknown_var", cmd[1]);
				
				int i = CommandLM.parseInt(sender, cmd[3]);
				
				byte comp = NumberComparator.getComparator(cmd[2]);
				if(NumberComparator.compare(var.intValue(), i, comp))
				{ gotoFunc(cmd[4]); return; }
				else if(cmd.length >= 6)
				{ gotoFunc(cmd[5]); return; }
			}
			else if(cmd[0].equals("ifBlock"))
			{
				CommandLM.checkArgs(cmd, 7);
				
				int x = CommandLM.parseRelInt(sender, sender.pos.posX, cmd[1]);
				int y = CommandLM.parseRelInt(sender, sender.pos.posY, cmd[2]);
				int z = CommandLM.parseRelInt(sender, sender.pos.posZ, cmd[3]);
				
				if(sender.world.blockExists(x, y, z))
				{
					int m = cmd[5].equals("*") ? -1 : CommandLM.parseInt(sender, cmd[5]);
					
					if(sender.world.getBlock(x, y, z) == CommandLM.getBlockByText(sender, cmd[4]) && (m == -1 || m == sender.world.getBlockMetadata(x, y, z)))
					{ gotoFunc(cmd[6]); return; }
					else if(cmd.length >= 8)
					{ gotoFunc(cmd[7]); return; }
				}
			}
			else if(cmd[0].equals("ifRedstone"))
			{
				//0-cmd
				//1-x
				//2-y
				//3-z
				//4-comp
				//5-val
				//6-func
				//7-else-func
				
				CommandLM.checkArgs(cmd, 7);
				
				int x = CommandLM.parseRelInt(sender, sender.pos.posX, cmd[1]);
				int y = CommandLM.parseRelInt(sender, sender.pos.posY, cmd[2]);
				int z = CommandLM.parseRelInt(sender, sender.pos.posZ, cmd[3]);
				
				if(sender.world.blockExists(x, y, z))
				{
					int rs = sender.world.getStrongestIndirectPower(x, y, z);
					int val = CommandLM.parseIntBounded(sender, cmd[5], 0, 15);
					
					byte comp = NumberComparator.getComparator(cmd[4]);
					if(NumberComparator.compare(rs, val, comp))
					{ gotoFunc(cmd[6]); return; }
					else if(cmd.length >= 8)
					{ gotoFunc(cmd[7]); return; }
				}
			}
			else if(cmd[0].equals("setblock"))
			{
				CommandLM.checkArgs(cmd, 5);
				
				int x = CommandLM.parseRelInt(sender, sender.pos.posX, cmd[1]);
				int y = CommandLM.parseRelInt(sender, sender.pos.posY, cmd[2]);
				int z = CommandLM.parseRelInt(sender, sender.pos.posZ, cmd[3]);
				
				if(sender.world.blockExists(x, y, z))
				{
					Block block = CommandLM.getBlockByText(sender, cmd[4]);
					int m = (cmd.length < 6) ? 0 : CommandLM.parseInt(sender, cmd[5]);
					
					if(m != sender.world.getBlockMetadata(x, y, z) || sender.world.getBlock(x, y, z) != block)
						sender.world.setBlock(x, y, z, block, m, 3);
				}
			}
			else FTBLib.runCommand(sender, "/" + s);
		}
		
		currentLine++;
	}
	
	public void gotoFunc(String s)
	{
		if(s == null || s.isEmpty() || !file.funcs.keys.contains(s))
			throw new CommandException("command.cmdscripts.unknown_func", String.valueOf(s));
		lastGotoLine.add(currentLine);
		currentLine = file.funcs.get(s).intValue();
	}
	
	public void stop()
	{ stopped = true; }
	
	public boolean stopped()
	{ return stopped || currentLine >= lines.size(); }
	
	public int currentLine()
	{ return currentLine; }

	public boolean isSleeping()
	{ return sleepTimer > 0; }
	
	public static void setGlobalVariable(ICommandSender sender, String s, int i)
	{
		Integer val0I = globalVariables.get(s);
		boolean hasVal0 = val0I != null;
		int val0 = hasVal0 ? globalVariables.get(s).intValue() : 0;
		if(hasVal0 && val0 == i) return;
		
		globalVariables.put(s, Integer.valueOf(i));
		
		if((!hasVal0 || val0 != i) && ScriptFile.globalVariablesFile != null)
		{
			if(ScriptFile.globalVariablesFile.funcs.keys.contains("any"))
			{
				ScriptInstance inst = CmdScriptsEventHandler.runScript(ScriptFile.globalVariablesFile, sender, new String[] { s });
				inst.variables.put("val", Integer.valueOf(i));
				inst.variables.put("prev", Integer.valueOf(val0));
				inst.gotoFunc("any");
			}
			
			if(ScriptFile.globalVariablesFile.funcs.keys.contains(s + "_*"))
			{
				ScriptInstance inst = CmdScriptsEventHandler.runScript(ScriptFile.globalVariablesFile, sender, new String[] { s });
				inst.variables.put("val", Integer.valueOf(i));
				inst.variables.put("prev", Integer.valueOf(val0));
				inst.gotoFunc(s + "_*");
			}
			
			if(ScriptFile.globalVariablesFile.funcs.keys.contains(s + '_' + i))
			{
				ScriptInstance inst = CmdScriptsEventHandler.runScript(ScriptFile.globalVariablesFile, sender, new String[] { s });
				inst.variables.put("val", Integer.valueOf(i));
				inst.variables.put("prev", Integer.valueOf(val0));
				inst.gotoFunc(s + '_' + i);
			}
		}
	}
	
	public static void clearGlobalVariables(ICommandSender sender)
	{
		if(ScriptFile.globalVariablesFile != null)
		{
			ScriptInstance inst = CmdScriptsEventHandler.runScript(ScriptFile.globalVariablesFile, sender, new String[0]);
			inst.gotoFunc("cleared");
		}
	}
}