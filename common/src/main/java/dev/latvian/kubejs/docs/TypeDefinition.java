package dev.latvian.kubejs.docs;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class TypeDefinition
{
	public final DocumentationEvent event;
	public final Class<?> type;
	public final List<MemberDefinition<?>> members;
	public final List<TypeDefinition> parents;

	TypeDefinition(DocumentationEvent e, Class<?> c)
	{
		event = e;
		type = c;
		members = new ArrayList<>();
		parents = new ArrayList<>();

		if (c != Object.class)
		{
			parents.add(e.type(c.getSuperclass()));

			for (Class<?> i : c.getInterfaces())
			{
				parents.add(e.type(i));
			}
		}
	}

	public TypeDefinition field(String name, FieldDefinition.Factory method)
	{
		members.add(method.create(new FieldDefinition(this, name)));
		return this;
	}

	public TypeDefinition field(String name, Class<?> type, String... comment)
	{
		return field(name, t -> t.type(type).comment(comment));
	}

	public TypeDefinition method(String name, MethodDefinition.Factory method)
	{
		members.add(method.create(new MethodDefinition(this, name)));
		return this;
	}
}