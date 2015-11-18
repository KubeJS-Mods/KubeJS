package latmod.cmdscripts;

import latmod.lib.FastList;
import latmod.lib.util.FinalIDObject;

public class ScriptFile extends FinalIDObject
{
	public final FastList<String> commands;
	
	public ScriptFile(String ID)
	{ super(ID); commands = new FastList<String>(); }
}