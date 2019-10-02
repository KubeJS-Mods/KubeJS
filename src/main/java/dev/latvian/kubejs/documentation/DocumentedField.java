package dev.latvian.kubejs.documentation;

import dev.latvian.kubejs.integration.aurora.MethodBean;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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
	public final boolean getter;
	public final boolean setter;

	public DocumentedField(Documentation documentation, Field field)
	{
		name = field.getName();
		type = field.getType();
		actualType = field.getGenericType();

		Info infoAnnotation = field.getAnnotation(Info.class);
		info = infoAnnotation == null ? "" : infoAnnotation.value();

		getter = true;
		setter = !Modifier.isFinal(field.getModifiers());
	}

	public DocumentedField(Documentation documentation, MethodBean bean)
	{
		name = bean.name;
		type = bean.getType();
		actualType = bean.getActualType();
		info = bean.getInfo();
		getter = bean.methods[0] != null || bean.methods[1] != null;
		setter = bean.methods[2] != null;
	}

	@Override
	public int compareTo(DocumentedField o)
	{
		return name.compareToIgnoreCase(o.name);
	}
}