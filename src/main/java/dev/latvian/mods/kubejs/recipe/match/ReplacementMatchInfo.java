package dev.latvian.mods.kubejs.recipe.match;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaMap;
import dev.latvian.mods.rhino.type.RecordTypeInfo;
import dev.latvian.mods.rhino.type.TypeInfo;

import java.util.Map;

public record ReplacementMatchInfo(ReplacementMatch match, boolean exact) {
	public static final RecordTypeInfo TYPE_INFO = (RecordTypeInfo) TypeInfo.of(ReplacementMatchInfo.class);

	public static final ReplacementMatchInfo NONE = new ReplacementMatchInfo(ReplacementMatch.NONE, false);

	public static ReplacementMatchInfo wrap(Context cx, Object o, TypeInfo target) {
		if (o == null) {
			return NONE;
		} else if (o instanceof ReplacementMatchInfo h) {
			return h;
		} else if (o instanceof Map || o instanceof NativeJavaMap) {
			return (ReplacementMatchInfo) TYPE_INFO.wrap(cx, o, target);
		} else {
			var m = ReplacementMatch.wrap(cx, o);
			return m == ReplacementMatch.NONE ? NONE : new ReplacementMatchInfo(m, false);
		}
	}

	@Override
	public String toString() {
		return exact ? "{exact: true, match: " + match + "}" : String.valueOf(match);
	}
}
