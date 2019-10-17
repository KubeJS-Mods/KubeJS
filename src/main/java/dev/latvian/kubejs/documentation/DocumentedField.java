package dev.latvian.kubejs.documentation;

import dev.latvian.kubejs.MinecraftClass;
import dev.latvian.kubejs.integration.aurora.MethodBean;
import dev.latvian.kubejs.integration.aurora.MethodBeanName;

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
	public final boolean canSet;
	public final boolean isMinecraftClass;

	public DocumentedField(Documentation documentation, Field field)
	{
		name = field.getName();
		type = field.getType();
		actualType = field.getGenericType();

		Info infoAnnotation = field.getAnnotation(Info.class);
		info = infoAnnotation == null ? "" : infoAnnotation.value();

		canSet = !Modifier.isFinal(field.getModifiers());
		isMinecraftClass = field.isAnnotationPresent(MinecraftClass.class);
	}

	public DocumentedField(Documentation documentation, MethodBean bean)
	{
		name = bean.name.name;
		type = bean.getType();
		actualType = bean.getActualType();
		info = bean.getInfo();
		canSet = bean.methods.containsKey(MethodBeanName.Type.SET);
		isMinecraftClass = bean.isMinecraftClass();
	}

	@Override
	public int compareTo(DocumentedField o)
	{
		return name.compareToIgnoreCase(o.name);
	}
}