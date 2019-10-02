package dev.latvian.kubejs.documentation;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class DocumentedMethod implements Comparable<DocumentedMethod>
{
	public final String name;
	public final String id;
	public final Class returnType;
	public final Type actualReturnType;
	public final String[] paramNames;
	public final Class[] paramTypes;
	public final Type[] actualParamTypes;
	public final String info;
	public final int beanType;
	public final String beanName;

	public DocumentedMethod(Documentation documentation, Method method)
	{
		name = method.getName();
		returnType = method.getReturnType();
		actualReturnType = method.getGenericReturnType();
		Parameter[] parameters = method.getParameters();
		paramNames = new String[parameters.length];
		paramTypes = new Class[parameters.length];
		actualParamTypes = new Type[parameters.length];

		for (int i = 0; i < parameters.length; i++)
		{
			paramNames[i] = parameters[i].getName();
			paramTypes[i] = parameters[i].getType();
			actualParamTypes[i] = parameters[i].getParameterizedType();

			P p = parameters[i].getAnnotation(P.class);

			if (p != null)
			{
				paramNames[i] = p.value();
			}

			T t = parameters[i].getAnnotation(T.class);

			if (t != null)
			{
				paramTypes[i] = t.value();
				actualParamTypes[i] = paramTypes[i];
			}
		}

		if (paramNames.length == 1 && paramNames[0].equals("arg0"))
		{
			paramNames[0] = documentation.getPrettyName(paramTypes[0]).substring(0, 1).toLowerCase();
		}

		Info infoAnnotation = findInfo(method);
		info = infoAnnotation == null ? "" : infoAnnotation.value();

		StringBuilder idBuilder = new StringBuilder();

		idBuilder.append(name);

		for (int i = 0; i < parameters.length; i++)
		{
			idBuilder.append('_');
			idBuilder.append(documentation.getPrettyName(paramTypes[i]));
			idBuilder.append('_');
			idBuilder.append(paramNames[i]);
		}

		id = idBuilder.toString();

		if (paramNames.length == 0 && name.length() > 3 && name.startsWith("get"))
		{
			beanType = 0;
			beanName = name.substring(3, 4).toLowerCase() + name.substring(4);
		}
		else if (paramNames.length == 0 && name.length() > 2 && name.startsWith("is"))
		{
			beanType = 1;
			beanName = name.substring(2, 3).toLowerCase() + name.substring(3);
		}
		else if (paramNames.length == 1 && name.length() > 3 && name.startsWith("set"))
		{
			beanType = 2;
			beanName = name.substring(3, 4).toLowerCase() + name.substring(4);
		}
		else
		{
			beanType = -1;
			beanName = "";
		}
	}

	@Nullable
	private static Info findInfo(Method method)
	{
		Info infoAnnotation = method.getAnnotation(Info.class);

		if (infoAnnotation != null)
		{
			return infoAnnotation;
		}

		List<Class> list = new ArrayList<>();

		Class sc = method.getDeclaringClass().getSuperclass();

		if (sc != null)
		{
			list.add(sc);
		}

		for (Class c : method.getDeclaringClass().getInterfaces())
		{
			list.add(c);
		}

		for (Class c : list)
		{
			try
			{
				Method m = c.getDeclaredMethod(method.getName(), method.getParameterTypes());

				if (m != null)
				{
					Info i = findInfo(m);

					if (i != null)
					{
						return i;
					}
				}


			}
			catch (Exception ex)
			{
			}
		}

		return null;
	}

	@Override
	public int compareTo(DocumentedMethod o)
	{
		return name.compareToIgnoreCase(o.name);
	}
}