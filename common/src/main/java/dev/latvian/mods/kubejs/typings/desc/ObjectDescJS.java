package dev.latvian.mods.kubejs.typings.desc;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public record ObjectDescJS(List<Pair<String, TypeDescJS>> types) implements TypeDescJS {
	@Override
	public void build(StringBuilder builder) {
		builder.append('{');

		for (int i = 0; i < types.size(); i++) {
			if (i > 0) {
				builder.append(',');
				builder.append(' ');
			}

			builder.append(types.get(i).getLeft());
			builder.append(':');
			builder.append(' ');
			types.get(i).getRight().build(builder);
		}

		builder.append('}');
	}

	@Override
	public String toString() {
		return build();
	}
}
