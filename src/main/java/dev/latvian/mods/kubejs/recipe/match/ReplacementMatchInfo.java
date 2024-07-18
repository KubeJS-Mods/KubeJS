package dev.latvian.mods.kubejs.recipe.match;

import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaMap;
import dev.latvian.mods.rhino.type.RecordTypeInfo;
import dev.latvian.mods.rhino.type.TypeInfo;

import java.util.Map;
import java.util.Optional;

public record ReplacementMatchInfo(ReplacementMatch match, boolean exact, Optional<RecipeComponent<?>> componentType) {
	public static final RecordTypeInfo TYPE_INFO = (RecordTypeInfo) TypeInfo.of(ReplacementMatchInfo.class);

	public static final ReplacementMatchInfo NONE = new ReplacementMatchInfo(ReplacementMatch.NONE, false, Optional.empty());

	public static ReplacementMatchInfo wrap(Context cx, Object o, TypeInfo target) {
		if (o == null) {
			return NONE;
		} else if (o instanceof ReplacementMatchInfo h) {
			return h;
		} else if (o instanceof Map || o instanceof NativeJavaMap) {
			return (ReplacementMatchInfo) TYPE_INFO.wrap(cx, o, target);
		} else {
			var m = ReplacementMatch.wrap(cx, o);
			return m == ReplacementMatch.NONE ? NONE : new ReplacementMatchInfo(m, false, Optional.empty());
		}
	}

	@Override
	public String toString() {
		return (exact || componentType.isPresent()) ? "{exact: " + exact + ", componentType: " + componentType.orElse(null) + ", match: " + match + "}" : String.valueOf(match);
	}
}
