package dev.latvian.mods.kubejs.typings.desc;

public record GenericDescJS(TypeDescJS type, TypeDescJS... types) implements TypeDescJS {
	@Override
	public void build(StringBuilder builder) {
		type.build(builder);
		builder.append('<');

		for (int i = 0; i < types.length; i++) {
			if (i > 0) {
				builder.append(',');
				builder.append(' ');
			}

			types[i].build(builder);
		}

		builder.append('>');
	}

	@Override
	public String toString() {
		return build();
	}
}
