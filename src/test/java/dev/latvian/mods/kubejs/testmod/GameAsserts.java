package dev.latvian.mods.kubejs.testmod;

import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;

import static org.assertj.core.api.Assertions.assertThat;

/// Bridges AssertJ into the game-test framework so failures carry AssertJ's rich message (e.g. the
/// actual vs expected count). A raw [AssertionError] is an [Error] the framework never catches - it
/// crashes the server - so these rethrow any assertion failure as a [GameTestAssertException], which
/// the framework reports, and which `thenWaitUntil` retries against, like any native game-test assert.
public final class GameAsserts {
	private GameAsserts() {
	}

	/// Runs {@code assertions}, converting any [AssertionError] into a [GameTestAssertException].
	public static void assertj(GameTestHelper helper, Runnable assertions) {
		try {
			assertions.run();
		} catch (AssertionError error) {
			throw new GameTestAssertException(Component.literal(String.valueOf(error.getMessage())), (int) helper.getTick());
		}
	}

	/// Asserts {@code id} was reported at least once.
	public static void assertFired(GameTestHelper helper, String id) {
		assertj(helper, () -> assertThat(TestRuntime.passed(id)).as("%s should have fired", id).isTrue());
	}

	/// Asserts {@code id} was reported exactly {@code expected} times, naming the actual count on mismatch.
	public static void assertCount(GameTestHelper helper, String id, int expected) {
		assertj(helper, () -> assertThat(TestRuntime.count(id)).as("%s fire count", id).isEqualTo(expected));
	}

	/// Surfaces any script-side assertion captured under {@code id} (see [TestRuntime#check]).
	public static void assertVerified(GameTestHelper helper, String id) {
		assertj(helper, () -> TestRuntime.verify(id));
	}
}
