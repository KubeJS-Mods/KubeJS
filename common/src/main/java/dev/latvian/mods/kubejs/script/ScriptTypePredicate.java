package dev.latvian.mods.kubejs.script;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public interface ScriptTypePredicate extends Predicate<ScriptType> {
	ScriptTypePredicate ALL = type -> true;
	ScriptTypePredicate COMMON = type -> type != ScriptType.STARTUP;
	ScriptTypePredicate STARTUP_OR_CLIENT = type -> type != ScriptType.SERVER;
	ScriptTypePredicate STARTUP_OR_SERVER = type -> type != ScriptType.CLIENT;

	@Override
	boolean test(ScriptType type);

	default List<ScriptType> getValidTypes() {
		if (this == ALL) {
			return Arrays.asList(ScriptType.VALUES);
		} else if (this == COMMON) {
			return List.of(ScriptType.SERVER, ScriptType.CLIENT);
		} else if (this == STARTUP_OR_CLIENT) {
			return List.of(ScriptType.STARTUP, ScriptType.CLIENT);
		} else if (this == STARTUP_OR_SERVER) {
			return List.of(ScriptType.STARTUP, ScriptType.SERVER);
		}

		var list = new ArrayList<ScriptType>(2);

		for (var type : ScriptType.VALUES) {
			if (test(type)) {
				list.add(type);
			}
		}

		return list;
	}
}
