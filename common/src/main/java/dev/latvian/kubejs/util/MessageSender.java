package dev.latvian.kubejs.util;

import dev.latvian.kubejs.text.Text;
import net.minecraft.network.chat.Component;

/**
 * @author LatvianModder
 */
public interface MessageSender
{
	Text getName();

	default Text getDisplayName()
	{
		return getName();
	}

	void tell(Component message);

	default void setStatusMessage(Component message)
	{
	}

	int runCommand(String command);

	default int runCommandSilent(String command)
	{
		return runCommand(command);
	}
}