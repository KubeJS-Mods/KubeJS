package dev.latvian.mods.kubejs.testmod;

import dev.latvian.mods.kubejs.color.KubeColor;
import dev.latvian.mods.kubejs.entity.KubeEntityEvent;
import dev.latvian.mods.kubejs.level.LevelBlock;
import dev.latvian.mods.kubejs.testmod.assertion.KubeEntityEventAssert;
import dev.latvian.mods.kubejs.testmod.assertion.LevelBlockAssert;
import dev.latvian.mods.kubejs.util.Tristate;
import dev.latvian.mods.rhino.Undefined;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractDoubleAssert;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.IterableAssert;
import org.assertj.core.api.ObjectAssert;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/// Bound into scripts as {@code TestRuntime} so a script can report a passing condition back to
/// the game test that drives it, e.g. {@code TestRuntime.pass('block.break.dirt')}.
///
/// Markers are counted (so a test can assert an event fired once or repeatedly) and stored in
/// concurrent collections because {@link #pass} runs on the server thread as events fire while the
/// game test polls from its own sequence. A test clears only the marker(s) it owns via {@link #clear}
/// so parallel or reordered tests can't wipe each other's in-flight markers. Startup-fired markers
/// (e.g. {@code BlockEvents.modification}) use {@link #passStartup} so they survive other tests' clears.
///
/// A script can also assert on the event object with AssertJ via {@link #assertThat}, wrapped in a
/// {@link #check} block, e.g. {@code TestRuntime.check('block.broken', () => assertThat(event.block).hasId('minecraft:dirt'))}.
/// The event dispatcher swallows exceptions thrown by a handler, so {@link #check} captures any failure
/// under the marker and {@link #verify} re-throws it on the game-test thread with AssertJ's message.
public class TestRuntime {
	private static final Map<String, Integer> COUNTS = new ConcurrentHashMap<>();
	private static final Set<String> STARTUP = ConcurrentHashMap.newKeySet();
	private static final Map<String, AssertionError> FAILURES = new ConcurrentHashMap<>();

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
			FAILURES.remove(id);
		}
	}

	public static void passStartup(String id) {
		STARTUP.add(id);
	}

	public static boolean passedStartup(String id) {
		return STARTUP.contains(id);
	}

	/// Marks {@code id} reached and runs the script's assertions, capturing any failure they throw so
	/// it survives to [#verify]. Catches [Throwable] (not just [AssertionError]) because a failure at
	/// script-load time would otherwise abort loading, and Rhino can surface an assertion wrapped in
	/// its own exception type - [#asAssertionError] unwraps it back to the underlying [AssertionError].
	public static void check(String id, Runnable assertions) {
		pass(id);

		try {
			assertions.run();
		} catch (Throwable error) {
			FAILURES.put(id, asAssertionError(error));
		}
	}

	private static AssertionError asAssertionError(Throwable error) {
		for (var current = error; current != null; current = current.getCause()) {
			if (current instanceof AssertionError assertionError) {
				return assertionError;
			}

			if (current.getCause() == current) {
				break;
			}
		}

		return new AssertionError(error.toString(), error);
	}

	/// Re-throws any assertion failure captured under {@code id}, so the game test fails on its own
	/// thread with AssertJ's message instead of merely timing out.
	public static void verify(String id) {
		var error = FAILURES.get(id);

		if (error != null) {
			throw error;
		}
	}

	/// Fails if a script-read value is `null` or JS `undefined` - used by the *KJS getter fixtures to
	/// prove the exposed properties actually resolve, not merely that calling them doesn't throw.
	public static void assertDefined(String label, @Nullable Object value) {
		if (value == null || Undefined.isUndefined(value)) {
			throw new AssertionError("Expected '" + label + "' to be defined but was: " + value);
		}
	}

	public static AbstractStringAssert<?> assertThat(String actual) {
		return Assertions.assertThat(actual);
	}

	public static AbstractBooleanAssert<?> assertThat(boolean actual) {
		return Assertions.assertThat(actual);
	}

	public static AbstractDoubleAssert<?> assertThat(double actual) {
		return Assertions.assertThat(actual);
	}

	public static KubeEntityEventAssert assertThat(KubeEntityEvent actual) {
		return new KubeEntityEventAssert(actual);
	}

	public static LevelBlockAssert assertThat(LevelBlock actual) {
		return new LevelBlockAssert(actual);
	}

	public static <T> IterableAssert<T> assertThat(Iterable<? extends T> actual) {
		return Assertions.assertThat(actual);
	}

	public static ObjectAssert<Object> assertThat(@Nullable Object actual) {
		return Assertions.assertThat(actual);
	}

	/// Boundaries for the JS-driven wrapper tests. Each declares a wrapped Java type, so passing a raw
	/// JS value invokes the registered type wrapper and returns the coerced object for the script to
	/// assert on - exercising the JS->Java conversion end to end, not a static `wrap` call.

	public static Vec3 asVec3(Vec3 value) {
		return value;
	}

	public static BlockPos asBlockPos(BlockPos value) {
		return value;
	}

	public static ItemStack asItemStack(ItemStack value) {
		return value;
	}

	public static MutableComponent asComponent(MutableComponent value) {
		return value;
	}

	public static CompoundTag asCompoundTag(CompoundTag value) {
		return value;
	}

	public static KubeColor asColor(KubeColor value) {
		return value;
	}

	public static Identifier asId(Identifier value) {
		return value;
	}

	public static UUID asUUID(UUID value) {
		return value;
	}

	public static Tristate asTristate(Tristate value) {
		return value;
	}

	public static Duration asDuration(Duration value) {
		return value;
	}

	public static Pattern asPattern(Pattern value) {
		return value;
	}

	public static IntProvider asIntProvider(IntProvider value) {
		return value;
	}

	public static MapColor asMapColor(MapColor value) {
		return value;
	}
}
