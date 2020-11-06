package dev.latvian.kubejs.util;

import dev.latvian.kubejs.text.Text;

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

	void tell(Object message);

	default void setStatusMessage(Object message)
	{
	}

	int runCommand(String command);
}