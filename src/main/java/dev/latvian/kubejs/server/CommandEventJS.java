package dev.latvian.kubejs.server;

import com.mojang.brigadier.ParseResults;
import net.minecraft.command.CommandSource;
import net.minecraftforge.event.CommandEvent;

/**
 * @author LatvianModder
 */
public class CommandEventJS extends ServerEventJS
{
	public final CommandEvent event;

	public CommandEventJS(CommandEvent e)
	{
		event = e;
	}

	@Override
	public boolean canCancel()
	{
		return true;
	}

	public ParseResults<CommandSource> getParseResults()
	{
		return event.getParseResults();
	}

	public void setParseResults(ParseResults<CommandSource> parse)
	{
		event.setParseResults(parse);
	}

	public Throwable getException()
	{
		return event.getException();
	}

	public void setException(Throwable exception)
	{
		event.setException(exception);
	}
}