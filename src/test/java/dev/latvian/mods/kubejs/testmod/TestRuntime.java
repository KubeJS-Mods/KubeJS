package dev.latvian.mods.kubejs.testmod;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/// Bound into scripts as {@code TestRuntime} so a script can report a passing condition back to
/// the game test that drives it, e.g. {@code TestRuntime.pass('block.break.dirt')}.
///
/// Markers are counted (so a test can assert an event fired once or repeatedly) and stored in
/// concurrent collections because {@link #pass} runs on the server thread as events fire while the
/// game test polls from its own sequence. A test clears only the marker(s) it owns via {@link #clear}
/// so parallel or reordered tests can't wipe each other's in-flight markers. Startup-fired markers
/// (e.g. {@code BlockEvents.modification}) use {@link #passStartup} so they survive other tests' clears.
public class TestRuntime {
	private static final Map<String, Integer> COUNTS = new ConcurrentHashMap<>();
	private static final Set<String> STARTUP = ConcurrentHashMap.newKeySet();

	public static void pass(String id) {
		COUNTS.merge(id, 1, Integer::sum);
	}

	public static boolean passed(String id) {
		return COUNTS.containsKey(id);
	}

	public static int count(String id) {
		return COUNTS.getOrDefault(id, 0);
	}

	public static void clear(String... ids) {
		for (var id : ids) {
			COUNTS.remove(id);
		}
	}

	public static void passStartup(String id) {
		STARTUP.add(id);
	}

	public static boolean passedStartup(String id) {
		return STARTUP.contains(id);
	}
}
