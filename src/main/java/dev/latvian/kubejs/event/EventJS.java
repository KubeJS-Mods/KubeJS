package dev.latvian.kubejs.event;

import dev.latvian.kubejs.documentation.Documentation;
import dev.latvian.kubejs.documentation.Ignore;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;

/**
 * @author LatvianModder
 */
public class EventJS
{
	private boolean cancelled = false;

	public boolean canCancel()
	{
		return false;
	}

	public final void cancel()
	{
		cancelled = true;
	}

	@Ignore
	public final boolean isCancelled()
	{
		return cancelled;
	}

	@Override
	public String toString()
	{
		Class c = getClass();
		StringBuilder b = new StringBuilder("== " + Documentation.get().getPrettyName(c) + " ==");

		try
		{
			for (Field field : c.getFields())
			{
				int m = field.getModifiers();

				if (Modifier.isPublic(m) && !Modifier.isStatic(m) && !Modifier.isTransient(m))
				{
					b.append('\n');
					b.append(field.getName());
					b.append(" = ");
					field.setAccessible(true);
					b.append(field.get(this));
				}
			}

			HashSet<String> removedMethods = new HashSet<>(Documentation.OBJECT_METHODS);
			removedMethods.add("cancel");
			removedMethods.add("isCancelled");
			removedMethods.add("canCancel");

			for (Method method : c.getMethods())
			{
				int m = method.getModifiers();

				if (Modifier.isPublic(m) && !Modifier.isStatic(m) && !removedMethods.contains(method.getName()))
				{
					b.append('\n');
					b.append(method.getName());
					b.append(method.getParameterCount() > 0 ? "(...)" : "()");

					if (method.getParameterCount() == 0 && method.getReturnType() != void.class)
					{
						b.append(" = ");
						method.setAccessible(true);
						b.append(method.invoke(this));
					}
				}
			}
		}
		catch (Exception ex)
		{
		}

		return b.toString();
	}
}