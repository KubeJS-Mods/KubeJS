package dev.latvian.kubejs.util;

import dev.latvian.kubejs.documentation.Ignore;
import dev.latvian.kubejs.documentation.P;
import org.apache.logging.log4j.Logger;

/**
 * @author LatvianModder
 */
public class LoggerWrapperJS
{
	private final Logger logger;

	public LoggerWrapperJS(Logger l)
	{
		logger = l;
	}

	@Ignore
	public String toString(Object text, Object... objects)
	{
		return objects.length == 0 ? String.valueOf(text) : String.format(String.valueOf(text), objects);
	}

	public void info(@P("text") Object text, @P("objects") Object... objects)
	{
		logger.info(toString(text, objects));
	}

	public void warn(@P("text") Object text, @P("objects") Object... objects)
	{
		logger.warn(toString(text, objects));
	}

	public void error(@P("text") Object text, @P("objects") Object... objects)
	{
		logger.error(toString(text, objects));
	}
}