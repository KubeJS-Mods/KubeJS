package latmod.cmdscripts;

import net.minecraft.command.CommandException;

@SuppressWarnings("serial")
public class ExceptionScriptNotFound extends CommandException
{
	public ExceptionScriptNotFound(String s)
	{ super("command.cmdscripts.not_found", new Object[] { s }); }
}