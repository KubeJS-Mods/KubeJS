package dev.latvian.kubejs.util;

import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

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

	public String toString(@Nullable Object text, Object... objects)
	{
		return objects.length == 0 ? String.valueOf(text) : String.format(String.valueOf(text), objects);
	}

	public void info(@Nullable Object text, Object... objects)
	{
		logger.info(toString(text, objects));
	}

	public void warn(@Nullable Object text, Object... objects)
	{
		logger.warn(toString(text, objects));
	}

	public void error(@Nullable Object text, Object... objects)
	{
		logger.error(toString(text, objects));
	}
}