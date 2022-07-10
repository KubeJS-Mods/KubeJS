package dev.latvian.mods.kubejs.server;

import com.mojang.brigadier.ParseResults;
import dev.architectury.event.events.common.CommandPerformEvent;
import dev.latvian.mods.kubejs.event.EventHandler;
import net.minecraft.commands.CommandSourceStack;

/**
 * @author LatvianModder
 */
public class CommandEventJS extends ServerEventJS {
	public static final EventHandler EVENT = EventHandler.server(CommandEventJS.class).cancelable().legacy("command.run");

	private final CommandPerformEvent event;

	public CommandEventJS(CommandPerformEvent e) {
		event = e;
	}

	public String getCommandName() {
		return event.getResults().getContext().getRootNode().getName();
	}

	public String getInput() {
		return event.getResults().getReader().getString();
	}

	public ParseResults<CommandSourceStack> getParseResults() {
		return event.getResults();
	}

	public void setParseResults(ParseResults<CommandSourceStack> parse) {
		event.setResults(parse);
	}

	public Throwable getException() {
		return event.getThrowable();
	}

	public void setException(Throwable exception) {
		event.setThrowable(exception);
	}
}