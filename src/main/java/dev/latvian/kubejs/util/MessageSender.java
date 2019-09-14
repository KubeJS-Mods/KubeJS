package dev.latvian.kubejs.util;

import dev.latvian.kubejs.documentation.Info;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.documentation.T;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.text.TextString;

/**
 * @author LatvianModder
 */
@Info("Anything that can send messages or run commands, usually player or server")
public interface MessageSender
{
	String getName();

	default Text getDisplayName()
	{
		return new TextString(getName());
	}

	@Info("Tell message in chat")
	void tell(@P("text") @T(Text.class) Object message);

	@Info("Set status message")
	default void setStatusMessage(@P("text") @T(Text.class) Object message)
	{
	}

	@Info("Runs command as if the sender was running it, ignoring permissions")
	int runCommand(@P("command") String command);
}