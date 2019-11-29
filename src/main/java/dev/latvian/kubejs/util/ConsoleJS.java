package dev.latvian.kubejs.util;

import dev.latvian.kubejs.script.ScriptFile;
import dev.latvian.kubejs.script.ScriptType;
import jdk.nashorn.internal.runtime.ECMAErrors;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * @author LatvianModder
 */
public class ConsoleJS
{
	private final ScriptType type;
	public final Logger logger;
	private String group;

	public ConsoleJS(ScriptType m, Logger log)
	{
		type = m;
		logger = log;
		group = "";
	}

	protected boolean shouldPrint()
	{
		return true;
	}

	public boolean isDebug()
	{
		return false;
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

		boolean debug = isDebug();

		if (!debug && group.isEmpty())
		{
			return s;
		}

		StringBuilder builder = new StringBuilder();

		if (debug)
		{
			ScriptFile f = type.manager.get().currentFile;

			if (f != null)
			{
				builder.append(f.info.location);
			}

			builder.append(':');
			builder.append(getScriptLine());
			builder.append(": ");
		}

		builder.append(group);
		builder.append(s);
		return builder.toString();
	}

	private String string(Object object, Object... args)
	{
		return string(String.format(String.valueOf(object), args));
	}

	public void info(Object message)
	{
		if (shouldPrint())
		{
			logger.info(string(message));
		}
	}

	public void infof(String message, Object... args)
	{
		if (shouldPrint())
		{
			logger.info(string(message, args));
		}
	}

	public void warn(Object message)
	{
		if (shouldPrint())
		{
			logger.warn(string(message));
		}
	}

	public void warnf(String message, Object... args)
	{
		if (shouldPrint())
		{
			logger.warn(string(message, args));
		}
	}

	public void error(Object message)
	{
		if (shouldPrint())
		{
			logger.error(string(message));
		}
	}

	public void errorf(String message, Object... args)
	{
		if (shouldPrint())
		{
			logger.error(string(message, args));
		}
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

	public int getScriptLine()
	{
		for (StackTraceElement element : Thread.currentThread().getStackTrace())
		{
			if (ECMAErrors.isScriptFrame(element))
			{
				return element.getLineNumber();
			}
		}

		return -1;
	}
}