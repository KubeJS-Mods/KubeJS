package dev.latvian.kubejs.documentation;

import dev.latvian.kubejs.MinecraftClass;
import dev.latvian.kubejs.integration.aurora.MethodBeanName;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
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
	public final Class returnType;
	public final Type actualReturnType;
	public final String[] paramNames;
	public final Class[] paramTypes;
	public final Type[] actualParamTypes;
	public final String info;
	public final String id;
	public final MethodBeanName bean;
	public final boolean isMinecraftClass;

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

		Info infoAnnotation = lookForAnnotation(method, Info.class);
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
		bean = MethodBeanName.get(name);
		isMinecraftClass = lookForAnnotation(method, MinecraftClass.class) != null;
	}

	@Nullable
	private static <T extends Annotation> T lookForAnnotation(Method method, Class<T> aclass)
	{
		T a = method.getAnnotation(aclass);

		if (a != null)
		{
			return a;
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
					T i = lookForAnnotation(m, aclass);

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