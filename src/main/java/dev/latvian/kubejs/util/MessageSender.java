package dev.latvian.kubejs.util;

import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.text.TextUtilsJS;

/**
 * @author LatvianModder
 */
public interface MessageSender
{
	void tell(Text text);

	default void tell(Object text)
	{
		tell(TextUtilsJS.INSTANCE.of(text));
	}

	int runCommand(String command);
}