package dev.latvian.mods.kubejs.recipe.filter;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class RegexIDFilter implements RecipeFilter {
	private final Pattern pattern;
	private final ConcurrentHashMap<ResourceLocation, Boolean> matchCache = new ConcurrentHashMap<>();

	private static Interner<RegexIDFilter> INTERNER;

	static {
		clearInternCache();
	}

	private RegexIDFilter(Pattern i) {
		pattern = i;
	}

	public static RegexIDFilter of(Pattern i) {
		return INTERNER.intern(new RegexIDFilter(i));
	}

	public static void clearInternCache() {
		INTERNER = Interners.newStrongInterner();
	}

	@Override
	public boolean test(RecipeMatchContext cx) {
		return matchCache.computeIfAbsent(cx.recipe().kjs$getOrCreateId(), location -> pattern.matcher(location.toString()).find());
	}

	@Override
	public String toString() {
		return "RegexIDFilter{" +
			"pattern=" + pattern +
			'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		RegexIDFilter that = (RegexIDFilter) o;
		return pattern.pattern().equals(that.pattern.pattern()) && pattern.flags() == that.pattern.flags();
	}

	@Override
	public int hashCode() {
		return Objects.hash(pattern.pattern(), pattern.flags());
	}
}
