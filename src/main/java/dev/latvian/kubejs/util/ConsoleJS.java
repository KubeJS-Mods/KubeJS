package dev.latvian.kubejs.util;

import dev.latvian.kubejs.script.ScriptFile;
import dev.latvian.kubejs.script.ScriptType;
import jdk.nashorn.internal.runtime.ECMAErrors;
import org.apache.logging.log4j.Logger;

/**
 * @author LatvianModder
 */
public class ConsoleJS
{
	private final ScriptType type;
	public final Logger logger;
	private String group;
	private int lineNumber;

	public ConsoleJS(ScriptType m, Logger log)
	{
		type = m;
		logger = log;
		group = "";
		lineNumber = 0;
	}

	protected boolean shouldPrint()
	{
		return true;
	}

	public void setLineNumber(boolean b)
	{
		lineNumber = Math.max(0, lineNumber + (b ? 1 : -1));
	}

	private String string(Object object)
	{
		Object o = UtilsJS.wrap(object, JSObjectType.ANY);
		String s = o == null || o instanceof String || o instanceof Number || o instanceof WrappedJS ? String.valueOf(o) : (o + " [" + o.getClass().getName() + "]");

		if (lineNumber == 0 && group.isEmpty())
		{
			return s;
		}

		StringBuilder builder = new StringBuilder();

		if (lineNumber > 0)
		{
			int ln = getScriptLine();

			if (ln != -1)
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

	public void log(Object message)
	{
		info(message);
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

	public void debug(Object message)
	{
		if (shouldPrint())
		{
			logger.debug(string(message));
		}
	}

	public void debugf(String message, Object... args)
	{
		if (shouldPrint())
		{
			logger.debug(string(message, args));
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