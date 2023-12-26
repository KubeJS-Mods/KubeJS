package dev.latvian.mods.kubejs.typings.desc;

import java.util.List;
import java.util.regex.Pattern;

public record ObjectDescJS(List<Entry> types) implements TypeDescJS {
	public record Entry(String key, TypeDescJS value, boolean optional, boolean wrap) {
		private static final Pattern ILLEGAL_KEY_PATTERN = Pattern.compile("[^a-zA-Z0-9_$]");

		public Entry(String key, TypeDescJS value, boolean optional) {
			this(key, value, optional, ILLEGAL_KEY_PATTERN.matcher(key).find());
		}
	}

	public ObjectDescJS add(String key, TypeDescJS value) {
		types.add(new Entry(key, value, false));
		return this;
	}

	public ObjectDescJS add(String key, TypeDescJS value, boolean optional) {
		types.add(new Entry(key, value, optional));
		return this;
	}

	@Override
	public void build(StringBuilder builder) {
		builder.append('{');

		for (int i = 0; i < types.size(); i++) {
			if (i > 0) {
				builder.append(',');
				builder.append(' ');
			}

			if (types.get(i).wrap) {
				builder.append('"');
			}

			builder.append(types.get(i).key);

			if (types.get(i).wrap) {
				builder.append('"');
			}

			if (types.get(i).optional) {
				builder.append('?');
			}

			builder.append(':');
			builder.append(' ');
			types.get(i).value.build(builder);
		}

		builder.append('}');
	}

	@Override
	public String toString() {
		return build();
	}
}
