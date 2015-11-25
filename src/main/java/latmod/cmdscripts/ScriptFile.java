package latmod.cmdscripts;

import latmod.lib.*;
import latmod.lib.util.FinalIDObject;

public class ScriptFile extends FinalIDObject
{
	public static ScriptFile startupFile = null;
	public static ScriptFile globalVariablesFile = null;
	
	public final FastList<String> lines;
	public final IntList ignored;
	public final FastMap<String, Integer> funcs;
	
	public ScriptFile(String ID)
	{
		super(ID);
		lines = new FastList<String>();
		ignored = new IntList();
		funcs = new FastMap<String, Integer>();
	}
	
	public void compile(FastList<String> list)
	{
		for(int i = 0; i < list.size(); i++)
		{
			String s1 = list.get(i).trim();
			lines.add(s1);
			
			if(!s1.isEmpty() && s1.charAt(0) != '#')
			{
				if(s1.indexOf(' ') != -1)
				{
					String[] cmd = s1.split(" ");
					
					if(cmd.length == 2 && cmd[0].equals("func"))
						funcs.put(cmd[1], Integer.valueOf(i + 1));
				}
			}
			else ignored.add(i);
		}
	}
}