package latmod.cmdscripts;

import ftb.lib.FTBLib;
import ftb.lib.cmd.CommandLM;
import latmod.lib.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.MathHelper;

public class ScriptInstance
{
	public final int ID;
	public final ScriptFile file;
	public final ICommandSender sender;
	public final FastMap<String, Integer> variables;
	private int currentLine = 0;
	private int sleepTimer = 0;
	private boolean stopped = false;
	
	public ScriptInstance(int i, ScriptFile f, ICommandSender s)
	{
		ID = i;
		file = f;
		sender = s;
		variables = new FastMap<String, Integer>();
	}
	
	public int hashCode()
	{ return ID; }
	
	public boolean equals(Object o)
	{ return o != null && (o == this || hashCode() == o.hashCode()); }
	
	public String toString()
	{ return Integer.toHexString(ID); }
	
	public void runCurrentLine()
	{
		if(file.commands.isEmpty() || sender == null) { stop(); return; }
		else if(sleepTimer > 0) { sleepTimer--; return; }
		
		String s = file.commands.get(currentLine);
		
		if(s == null) { stop(); return; }
		
		while(s.isEmpty() || s.charAt(0) == '#')
		{
			currentLine++;
			s = file.commands.get(currentLine);
			if(s == null) { stop(); return; }
		}
		
		runLine(s, true);
	}
	
	private void runLine(String s, boolean nextLine)
	{
		String[] cmd = s.split(" ");
		
		if(cmd.length > 0)
		{
			if(cmd[0].equals("exit")) stop();
			else if(cmd[0].equals("goto"))
			{
				if(cmd.length == 1)
				{
					currentLine = CommandLM.parseIntBounded(sender, cmd[1], 1, file.commands.size());
					return;
				}
			}
			else if(cmd[0].equals("sleep"))
			{
				if(cmd.length == 2)
				{
					sleepTimer = CommandLM.parseIntWithMin(sender, cmd[1], 1);
				}
			}
			else if(cmd[0].equals("setVar"))
			{
				if(cmd.length == 3)
				{
					Integer i0 = variables.get(cmd[1]);
					int i = MathHelper.floor_double(CommandLM.func_110666_a(sender, (i0 == null) ? 0 : i0.intValue(), cmd[2]));
					variables.put(cmd[1], Integer.valueOf(i));
				}
			}
			else if(cmd[0].equals("ifVar"))
			{
				if(cmd.length >= 5)
				{
					Integer var = variables.get(cmd[1]);
					int i = CommandLM.parseInt(sender, cmd[3]);
					
					if(compareNumbers((var == null) ? 0 : var.intValue(), i, cmd[2]))
					{
						String cmd1 = LMStringUtils.unsplitSpaceUntilEnd(4, cmd);
						runLine(cmd1, false);
					}
				}
			}
			else
			{
				for(int i = 0; i < variables.size(); i++)
					s = s.replace('$' + variables.keys.get(i) + '$', Integer.toString(variables.values.get(i)));
				FTBLib.runCommand(sender, "/" + s);
			}
		}
		
		if(nextLine) currentLine++;
	}
	
	private boolean compareNumbers(int a, int b, String s)
	{
		if(s.equals("==")) return a == b;
		else if(s.equals("!=")) return a != b;
		else if(s.equals(">")) return a > b;
		else if(s.equals("<")) return a < b;
		else if(s.equals(">=")) return a >= b;
		else if(s.equals("<=")) return a <= b;
		else return false;
	}
	
	public void stop()
	{ stopped = true; }
	
	public boolean stopped()
	{ return stopped || currentLine >= file.commands.size(); }
	
	public int currentLine()
	{ return currentLine; }

	public boolean isSleeping()
	{ return sleepTimer > 0; }
}