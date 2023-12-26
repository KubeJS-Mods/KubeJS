package dev.latvian.mods.kubejs.typings.desc;

import java.util.ArrayList;

public sealed interface TypeDescJS permits ArrayDescJS, FixedArrayDescJS, GenericDescJS, ObjectDescJS, OrDescJS, PrimitiveDescJS {
	TypeDescJS ANY = new PrimitiveDescJS("any");
	TypeDescJS NULL = new PrimitiveDescJS("null");
	TypeDescJS STRING = new PrimitiveDescJS("string");
	TypeDescJS NUMBER = new PrimitiveDescJS("number");
	TypeDescJS BOOLEAN = new PrimitiveDescJS("boolean");
	TypeDescJS MAP = new PrimitiveDescJS("Map");
	TypeDescJS ANY_MAP = new GenericDescJS(MAP, STRING, ANY);

	static TypeDescJS fixedArray(TypeDescJS... types) {
		return new FixedArrayDescJS(types);
	}

	static TypeDescJS any(TypeDescJS... types) {
		return new OrDescJS(types);
	}

	static ObjectDescJS object() {
		return new ObjectDescJS(new ArrayList<>());
	}

	static ObjectDescJS object(int init) {
		return new ObjectDescJS(new ArrayList<>(init));
	}

	void build(StringBuilder builder);

	default String build() {
		var builder = new StringBuilder();
		build(builder);
		return builder.toString();
	}

	default TypeDescJS asArray() {
		return new ArrayDescJS(this);
	}

	default TypeDescJS asMap() {
		return asMap(STRING);
	}

	default TypeDescJS asMap(TypeDescJS key) {
		if (key == STRING && this == ANY) {
			return ANY_MAP;
		}

		return new GenericDescJS(MAP, key, this);
	}

	default TypeDescJS or(TypeDescJS type) {
		return new OrDescJS(new TypeDescJS[]{this, type});
	}

	default TypeDescJS withGenerics(TypeDescJS... types) {
		return new GenericDescJS(this, types);
	}
}
