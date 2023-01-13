package dev.latvian.mods.kubejs.recipe.filter;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * @author LatvianModder
 */
public class RegexIDFilter implements RecipeFilter {
	private final Pattern pattern;
	private final ConcurrentHashMap<ResourceLocation, Boolean> matchCache = new ConcurrentHashMap<>();

	private static final Interner<RegexIDFilter> INTERNER = Interners.newStrongInterner();

	private RegexIDFilter(Pattern i) {
		pattern = i;
	}

	public static RegexIDFilter of(Pattern i) {
		return INTERNER.intern(new RegexIDFilter(i));
	}

	@Override
	public boolean test(RecipeJS recipe) {
		return matchCache.computeIfAbsent(recipe.getOrCreateId(), location -> pattern.matcher(location.toString()).find());
	}

	@Override
	public String toString() {
		return "RegexIDFilter{" +
				"pattern=" + pattern +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RegexIDFilter that = (RegexIDFilter) o;
		return pattern.pattern().equals(that.pattern.pattern()) && pattern.flags() == that.pattern.flags();
	}

	@Override
	public int hashCode() {
		return Objects.hash(pattern.pattern(), pattern.flags());
	}
}
