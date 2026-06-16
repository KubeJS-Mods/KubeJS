package dev.latvian.mods.kubejs.testmod;

import java.util.HashSet;
import java.util.Set;

/// Bound into scripts as {@code TestRuntime} so a script can report a passing condition back to
/// the game test that drives it, e.g. {@code TestRuntime.pass('block.break.dirt')}.
public class TestRuntime {
	private static final Set<String> PASSED = new HashSet<>();

	public static void pass(String id) {
		PASSED.add(id);
	}

	public static boolean passed(String id) {
		return PASSED.contains(id);
	}

	public static void reset() {
		PASSED.clear();
	}
}
