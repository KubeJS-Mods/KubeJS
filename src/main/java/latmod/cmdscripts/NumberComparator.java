package latmod.cmdscripts;

import net.minecraft.command.CommandException;

public class NumberComparator
{
	private static final byte NONE = 0;
	private static final byte EQUALS = 1;
	private static final byte EQUALS_FALSE = 2;
	private static final byte SMALLER = 3;
	private static final byte LARGER = 4;
	private static final byte SMALLER_OE = 5;
	private static final byte LARGER_OE = 6;
	
	public static byte getComparator(String s)
	{
		byte b = getComparator0(s);
		if(b == NONE) throw new CommandException("command.cmdscripts.invalid_comp", s);
		return b;
	}
	
	public static byte getComparator0(String s)
	{
		if(s == null) return NONE;
		int siz = s.length();
		if(siz == 0 || siz > 2) return NONE;
		
		char c1 = s.charAt(0);
		
		if(siz == 1)
		{
			if(c1 == '<') return SMALLER;
			else if(c1 == '>') return LARGER;
		}
		else
		{
			char c2 = s.charAt(1);
			
			if(c2 == '=')
			{
				if(c1 == '=') return EQUALS;
				else if(c1 == '!') return EQUALS_FALSE;
				else if(c1 == '<') return SMALLER_OE;
				else if(c1 == '>') return LARGER_OE;
			}
		}
		
		return NONE;
	}
	
	public static boolean compare(int a, int b, byte comp)
	{
		if(comp == NONE) return false;
		else if(comp == EQUALS) return a == b;
		else if(comp == EQUALS_FALSE) return a != b;
		else if(comp == SMALLER) return a < b;
		else if(comp == LARGER) return a > b;
		else if(comp == SMALLER_OE) return a <= b;
		else if(comp == LARGER_OE) return a >= b;
		else return false;
	}
}