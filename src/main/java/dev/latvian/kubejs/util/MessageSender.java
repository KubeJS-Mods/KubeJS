package dev.latvian.kubejs.util;

import dev.latvian.kubejs.documentation.DocClass;
import dev.latvian.kubejs.documentation.DocMethod;
import dev.latvian.kubejs.documentation.Param;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.text.TextString;

/**
 * @author LatvianModder
 */
@DocClass("Anything that can send messages or run commands, usually player or server")
public interface MessageSender
{
	@DocMethod
	String name();

	@DocMethod
	default Text displayName()
	{
		return new TextString(name());
	}

	@DocMethod(value = "Tell message in chat", params = @Param(value = "text", type = Text.class))
	void tell(Object message);

	@DocMethod(value = "Set status message", params = @Param(value = "text", type = Text.class))
	default void statusMessage(Object message)
	{
	}

	@DocMethod(value = "Runs command as if the sender was running it, ignoring permissions", params = @Param("command"))
	int runCommand(String command);
}