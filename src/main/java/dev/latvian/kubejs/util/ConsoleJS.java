package dev.latvian.kubejs.util;

import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * @author LatvianModder
 */
public class ConsoleJS
{
	public final Logger logger;
	private String group;

	public ConsoleJS(Logger log)
	{
		logger = log;
		group = "";
	}

	private String string(Object object)
	{
		String s;

		if (object instanceof Map)
		{
			s = JsonUtilsJS.toString(JsonUtilsJS.of(object));
		}
		else
		{
			s = String.valueOf(object);
		}

		if (group.isEmpty())
		{
			return s;
		}

		return group + s;
	}

	private String string(Object object, Object... args)
	{
		return string(String.format(String.valueOf(object), args));
	}

	public void info(Object message)
	{
		logger.info(string(message));
	}

	public void infof(String message, Object... args)
	{
		logger.info(string(message, args));
	}

	public void warn(Object message)
	{
		logger.warn(string(message));
	}

	public void warnf(String message, Object... args)
	{
		logger.warn(string(message, args));
	}

	public void error(Object message)
	{
		logger.error(string(message));
	}

	public void errorf(String message, Object... args)
	{
		logger.error(string(message, args));
	}

	public void group()
	{
		group += "  ";
	}

	public void groupEnd()
	{
		if (group.length() >= 2)
		{
			group = group.substring(0, group.length() - 2);
		}
	}

	public void trace()
	{
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		info("=== Stack Trace ===");

		for (StackTraceElement element : elements)
		{
			info("=\t" + element);
		}
	}
}