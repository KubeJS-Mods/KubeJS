package dev.latvian.kubejs.docs;

/**
 * @author LatvianModder
 */
public class FieldDefinition extends MemberDefinition<FieldDefinition>
{
	public interface Factory
	{
		FieldDefinition create(FieldDefinition f);
	}

	public boolean isFinal;

	FieldDefinition(TypeDefinition p, String n)
	{
		super(p, n);
		isFinal = true;
	}

	public FieldDefinition mutable()
	{
		isFinal = false;
		return this;
	}
}