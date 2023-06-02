package dev.latvian.mods.kubejs.typings.desc;

import java.util.Collection;
import java.util.Map;

public interface DescriptionContext {
	DescriptionContext DEFAULT = new DescriptionContext() {
	};

	DescriptionContext DISPLAY = new DescriptionContext() {
		@Override
		public String typeName(Class<?> type) {
			var n = type.getName();
			int i = n.lastIndexOf('.');
			return i != -1 ? n.substring(i + 1) : n;
		}
	};

	default TypeDescJS javaType(Class<?> type) {
		if (type == null) {
			return TypeDescJS.NULL;
		} else if (type == Object.class) {
			return TypeDescJS.ANY;
		} else if (Number.class.isAssignableFrom(type) || type == Byte.TYPE || type == Short.TYPE || type == Integer.TYPE || type == Long.TYPE || type == Float.TYPE || type == Double.TYPE) {
			return TypeDescJS.NUMBER;
		} else if (CharSequence.class.isAssignableFrom(type) || type == Character.class || type == Character.TYPE) {
			return TypeDescJS.STRING;
		} else if (type == Boolean.class || type == Boolean.TYPE) {
			return TypeDescJS.BOOLEAN;
		} else if (Map.class.isAssignableFrom(type)) {
			return TypeDescJS.ANY.asMap(TypeDescJS.ANY);
		} else if (type.isArray() || Collection.class.isAssignableFrom(type)) {
			return TypeDescJS.ANY.asArray();
		}

		return new PrimitiveDescJS(typeName(type));
	}

	default String typeName(Class<?> type) {
		return type.getName();
	}
}
