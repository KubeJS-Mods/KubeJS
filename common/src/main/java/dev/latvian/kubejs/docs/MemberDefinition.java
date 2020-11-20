package dev.latvian.kubejs.docs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author LatvianModder
 */
public class MemberDefinition<T extends MemberDefinition<T>>
{
	public final TypeDefinition parent;
	public final String name;
	public TypeDefinition type;
	public final List<String> comment;

	MemberDefinition(TypeDefinition p, String n)
	{
		parent = p;
		name = n;
		type = parent.event.type(void.class);
		comment = new ArrayList<>();
	}

	public T comment(String... c)
	{
		comment.addAll(Arrays.asList(c));
		return (T) this;
	}

	public T type(Class<?> t)
	{
		type = parent.event.type(t);
		return (T) this;
	}
}