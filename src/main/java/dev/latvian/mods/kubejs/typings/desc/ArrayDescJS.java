package dev.latvian.mods.kubejs.typings.desc;

public record ArrayDescJS(TypeDescJS type) implements TypeDescJS {
	@Override
	public void build(StringBuilder builder) {
		type.build(builder);
		builder.append('[');
		builder.append(']');
	}

	@Override
	public String toString() {
		return build();
	}
}
