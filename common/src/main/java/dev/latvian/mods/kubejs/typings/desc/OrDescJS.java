package dev.latvian.mods.kubejs.typings.desc;

public record OrDescJS(TypeDescJS[] types) implements TypeDescJS {
	@Override
	public void build(StringBuilder builder) {
		for (int i = 0; i < types.length; i++) {
			if (i > 0) {
				builder.append(" | ");
			}

			types[i].build(builder);
		}
	}

	@Override
	public TypeDescJS or(TypeDescJS type) {
		var types1 = new TypeDescJS[types.length + 1];
		System.arraycopy(types, 0, types1, 0, types.length);
		types1[types.length] = type;
		return new OrDescJS(types1);
	}

	@Override
	public String toString() {
		return build();
	}
}
