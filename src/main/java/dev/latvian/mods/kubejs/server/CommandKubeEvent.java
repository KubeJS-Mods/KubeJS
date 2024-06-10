package dev.latvian.mods.kubejs.server;

import com.mojang.brigadier.ParseResults;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.neoforge.event.CommandEvent;

public class CommandKubeEvent extends ServerKubeEvent {
	private final CommandEvent event;
	private final String commandName;

	public CommandKubeEvent(CommandEvent event) {
		super(event.getParseResults().getContext().getSource().getServer());
		this.event = event;
		this.commandName = event.getParseResults().getContext().getNodes().isEmpty() ? "" : event.getParseResults().getContext().getNodes().getFirst().getNode().getName();
	}

	public String getCommandName() {
		return commandName;
	}

	public String getInput() {
		return event.getParseResults().getReader().getString();
	}

	public ParseResults<CommandSourceStack> getParseResults() {
		return event.getParseResults();
	}

	public void setParseResults(ParseResults<CommandSourceStack> parse) {
		event.setParseResults(parse);
	}

	public Throwable getException() {
		return event.getException();
	}

	public void setException(Throwable exception) {
		event.setException(exception);
	}
}