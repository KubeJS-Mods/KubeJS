package dev.latvian.kubejs.integration.aurora;

import dev.latvian.kubejs.documentation.DocumentedMethod;

import javax.annotation.Nullable;
import java.lang.reflect.Type;

/**
 * @author LatvianModder
 */
public class MethodBean implements Comparable<MethodBean>
{
	//Getter emoji: &#x1F537;
	//Setter emoji: &#x1F536;

	public final String name;
	public DocumentedMethod[] methods = new DocumentedMethod[3];

	public MethodBean(String n)
	{
		name = n;
	}

	@Override
	public int compareTo(MethodBean o)
	{
		return name.compareToIgnoreCase(o.name);
	}

	public String getInfo()
	{
		for (DocumentedMethod m : methods)
		{
			if (m != null && !m.info.isEmpty())
			{
				return m.info;
			}
		}

		return "";
	}

	@Nullable
	public Class getType()
	{
		if (methods[0] != null)
		{
			return methods[0].returnType;
		}
		else if (methods[1] != null)
		{
			return boolean.class;
		}

		return methods[2] == null ? null : methods[2].paramTypes[0];
	}

	@Nullable
	public Type getActualType()
	{
		if (methods[0] != null)
		{
			return methods[0].actualReturnType;
		}
		else if (methods[1] != null)
		{
			return boolean.class;
		}

		return methods[2] == null ? null : methods[2].actualParamTypes[0];
	}
}