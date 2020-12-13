package dev.latvian.kubejs.server;

import com.mojang.brigadier.ParseResults;
import me.shedaniel.architectury.event.events.CommandPerformEvent;
import net.minecraft.commands.CommandSourceStack;

/**
 * @author LatvianModder
 */
public class CommandEventJS extends ServerEventJS
{
	private final CommandPerformEvent event;

	public CommandEventJS(CommandPerformEvent e)
	{
		event = e;
	}

	@Override
	public boolean canCancel()
	{
		return true;
	}

	public ParseResults<CommandSourceStack> getParseResults()
	{
		return event.getResults();
	}

	public void setParseResults(ParseResults<CommandSourceStack> parse)
	{
		event.setResults(parse);
	}

	public Throwable getException()
	{
		return event.getThrowable();
	}

	public void setException(Throwable exception)
	{
		event.setThrowable(exception);
	}
}