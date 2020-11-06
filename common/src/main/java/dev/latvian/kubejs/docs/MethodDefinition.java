package dev.latvian.kubejs.docs;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class MethodDefinition extends MemberDefinition<MethodDefinition>
{
	public interface Factory
	{
		MethodDefinition create(MethodDefinition m);
	}

	public final List<Pair<String, TypeDefinition>> params;

	MethodDefinition(TypeDefinition p, String n)
	{
		super(p, n);
		params = new ArrayList<>();
	}

	public MethodDefinition param(String name, Class<?> c)
	{
		params.add(Pair.of(name, parent.event.type(c)));
		return this;
	}
}