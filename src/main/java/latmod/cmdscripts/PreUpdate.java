package latmod.cmdscripts;

import java.io.File;

import latmod.lib.MathHelperLM;
import net.minecraft.command.*;

//FIXME: Remove me
public class PreUpdate
{
	//LMStringUtils
	public static String[] shiftArray(String[] s)
	{
		if(s == null || s.length == 0) return new String[0];
		String[] s1 = new String[s.length - 1];
		for(int i = 0; i < s1.length; i++) s1[i] = s[i + 1];
		return s1;
	}
	
	//CommandLM
	public static int parseRelInt(ICommandSender ics, int n, String s)
	{ return MathHelperLM.floor(CommandBase.func_110666_a(ics, n, s)); }
	
	//LMFileUtils
	public static String getRawFileName(File f)
	{
		if(f == null || !f.exists()) return null;
		else if(f.isDirectory()) return f.getName();
		else if(f.isFile())
		{
			String s = f.getName();
			return s.substring(0, s.lastIndexOf('.'));
		}
		return null;
	}
}