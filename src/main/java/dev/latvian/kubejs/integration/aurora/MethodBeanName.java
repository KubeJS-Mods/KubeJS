package dev.latvian.kubejs.integration.aurora;

import javax.annotation.Nullable;
import java.util.HashSet;

/**
 * @author LatvianModder
 */
public class MethodBeanName implements Comparable<MethodBeanName>
{
	public enum Type
	{
		GET,
		IS,
		SET;

		public boolean isValid(int paramCount)
		{
			return paramCount == 0 ? (this == Type.GET || this == Type.IS) : (paramCount == 1 && this == Type.SET);
		}
	}

	private static final HashSet<String> JAVA_KEYWORDS = new HashSet<>();

	static
	{
		JAVA_KEYWORDS.add("byte");
		JAVA_KEYWORDS.add("short");
		JAVA_KEYWORDS.add("int");
		JAVA_KEYWORDS.add("long");
		JAVA_KEYWORDS.add("float");
		JAVA_KEYWORDS.add("double");
		JAVA_KEYWORDS.add("char");
		JAVA_KEYWORDS.add("null");
		JAVA_KEYWORDS.add("void");
		JAVA_KEYWORDS.add("public");
		JAVA_KEYWORDS.add("protected");
		JAVA_KEYWORDS.add("private");
		JAVA_KEYWORDS.add("abstract");
		JAVA_KEYWORDS.add("final");
		JAVA_KEYWORDS.add("static");
		JAVA_KEYWORDS.add("for");
		JAVA_KEYWORDS.add("if");
		JAVA_KEYWORDS.add("else");
		JAVA_KEYWORDS.add("return");
		JAVA_KEYWORDS.add("while");
		JAVA_KEYWORDS.add("do");
		JAVA_KEYWORDS.add("try");
		JAVA_KEYWORDS.add("catch");
		JAVA_KEYWORDS.add("extends");
		JAVA_KEYWORDS.add("interface");
		JAVA_KEYWORDS.add("new");
		JAVA_KEYWORDS.add("instanceof");
	}

	@Nullable
	public static MethodBeanName get(String name)
	{
		if (name.length() > 3 && Character.isUpperCase(name.charAt(3)) && name.startsWith("get"))
		{
			return get0(Type.GET, name, Character.toLowerCase(name.charAt(3)) + name.substring(4));
		}
		else if (name.length() > 2 && Character.isUpperCase(name.charAt(2)) && name.startsWith("is"))
		{
			return get0(Type.IS, name, Character.toLowerCase(name.charAt(2)) + name.substring(3));
		}
		else if (name.length() > 3 && Character.isUpperCase(name.charAt(3)) && name.startsWith("set"))
		{
			return get0(Type.SET, name, Character.toLowerCase(name.charAt(3)) + name.substring(4));
		}

		return null;
	}

	@Nullable
	private static MethodBeanName get0(Type t, String o, String n)
	{
		if (n.isEmpty() || JAVA_KEYWORDS.contains(n))
		{
			return null;
		}

		return new MethodBeanName(t, o, n);
	}

	public final Type type;
	public final String originalName;
	public final String name;

	private MethodBeanName(Type t, String o, String n)
	{
		type = t;
		originalName = o;
		name = n;
	}

	@Override
	public int compareTo(MethodBeanName o)
	{
		return name.compareToIgnoreCase(o.name);
	}

	@Override
	public String toString()
	{
		return name;
	}

	@Override
	public int hashCode()
	{
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj == this || obj instanceof MethodBeanName && name.equals(obj.toString());
	}
}