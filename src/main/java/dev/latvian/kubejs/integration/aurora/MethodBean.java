package dev.latvian.kubejs.integration.aurora;

import dev.latvian.kubejs.documentation.DocumentedMethod;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class MethodBean implements Comparable<MethodBean>
{
	public final MethodBeanName name;
	public final Map<MethodBeanName.Type, DocumentedMethod> methods;

	public MethodBean(MethodBeanName n)
	{
		name = n;
		methods = new HashMap<>();
	}

	@Override
	public int compareTo(MethodBean o)
	{
		return name.compareTo(o.name);
	}

	public String getInfo()
	{
		for (DocumentedMethod m : methods.values())
		{
			if (!m.info.isEmpty())
			{
				return m.info;
			}
		}

		return "";
	}

	@Nullable
	public Class getType()
	{
		if (methods.containsKey(MethodBeanName.Type.GET))
		{
			return methods.get(MethodBeanName.Type.GET).returnType;
		}
		else if (methods.containsKey(MethodBeanName.Type.IS))
		{
			return boolean.class;
		}
		else if (methods.containsKey(MethodBeanName.Type.SET))
		{
			return methods.get(MethodBeanName.Type.SET).paramTypes[0];
		}

		return null;
	}

	@Nullable
	public Type getActualType()
	{
		if (methods.containsKey(MethodBeanName.Type.GET))
		{
			return methods.get(MethodBeanName.Type.GET).actualReturnType;
		}
		else if (methods.containsKey(MethodBeanName.Type.IS))
		{
			return boolean.class;
		}
		else if (methods.containsKey(MethodBeanName.Type.SET))
		{
			return methods.get(MethodBeanName.Type.SET).actualParamTypes[0];
		}

		return null;
	}

	public boolean isMinecraftClass()
	{
		for (DocumentedMethod method : methods.values())
		{
			if (method.isMinecraftClass)
			{
				return true;
			}
		}

		return false;
	}
}