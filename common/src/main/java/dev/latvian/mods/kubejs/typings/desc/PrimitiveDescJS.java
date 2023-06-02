package dev.latvian.mods.kubejs.typings.desc;

public record PrimitiveDescJS(String type) implements TypeDescJS {
	@Override
	public void build(StringBuilder builder) {
		builder.append(type);
	}

	@Override
	public String build() {
		return type;
	}

	@Override
	public String toString() {
		return type;
	}
}
