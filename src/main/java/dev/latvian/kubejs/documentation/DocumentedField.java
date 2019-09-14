package dev.latvian.kubejs.documentation;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * @author LatvianModder
 */
public class DocumentedField implements Comparable<DocumentedField>
{
	public final String name;
	public final Class type;
	public final Type actualType;
	public final String info;

	public DocumentedField(Documentation documentation, Field field)
	{
		name = field.getName();
		type = field.getType();
		actualType = field.getGenericType();

		Info infoAnnotation = field.getAnnotation(Info.class);
		info = infoAnnotation == null ? "" : infoAnnotation.value();
	}

	@Override
	public int compareTo(DocumentedField o)
	{
		return name.compareToIgnoreCase(o.name);
	}
}