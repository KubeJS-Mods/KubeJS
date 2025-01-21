package dev.latvian.mods.kubejs.recipe.component;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.regex.Pattern;

public record UniqueIdBuilder(StringBuilder builder) {
	public static final Pattern NON_W_PATTERN = Pattern.compile("\\W");
	public static final Pattern MULTIPLE_UNDERSCORES_PATTERN = Pattern.compile("_{2,}");

	public void appendSeparator() {
		builder.append('_');
	}

	public void append(@Nullable String string) {
		if (string != null && !string.isEmpty()) {
			builder.append('_');
			builder.append(string);
		}
	}

	public void append(@Nullable ResourceLocation id) {
		if (id == null) {
			return;
		}

		builder.append('_');

		if (!id.getNamespace().equals("minecraft") && !id.getNamespace().equals("kubejs")) {
			builder.append(id.getNamespace());
			builder.append('_');
		}

		builder.append(id.getPath());
	}

	public String build() {
		var result = builder.toString();
		builder.setLength(0);
		result = MULTIPLE_UNDERSCORES_PATTERN.matcher(NON_W_PATTERN.matcher(result.toLowerCase(Locale.ROOT)).replaceAll("_")).replaceAll("_");

		if (!result.isEmpty() && result.charAt(0) == '_') {
			result = result.substring(1);
		}

		if (!result.isEmpty() && result.charAt(result.length() - 1) == '_') {
			result = result.substring(0, result.length() - 1);
		}

		return result.isEmpty() ? null : result;
	}
}
