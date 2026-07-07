package dev.latvian.mods.kubejs.testmod.server;

import dev.latvian.mods.kubejs.testmod.TestRuntime;
import net.minecraft.world.level.GameType;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.GameTest;

@ForEachTest(groups = "kubejs.server.event")
public class ServerEventTests {
	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "server_tick", description = "KubeJS ServerEvents.tick fires while the server ticks")
	static void serverTick(final DynamicTest test) {
		test.onGameTest(helper -> helper.startSequence(() -> helper.makeTickingMockServerPlayerInCorner(GameType.SURVIVAL))
			.thenExecute(() -> TestRuntime.clear("server.tick"))
			.thenIdle(2)
			.thenWaitUntil(() -> helper.assertTrue(TestRuntime.passed("server.tick"), "script did not report server.tick"))
			.thenSucceed());
	}

	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "server_command", description = "KubeJS ServerEvents.command fires when a command runs")
	static void serverCommand(final DynamicTest test) {
		test.onGameTest(helper -> helper.startSequence(() -> helper.makeTickingMockServerPlayerInCorner(GameType.SURVIVAL))
			.thenExecute(() -> TestRuntime.clear("server.command"))
			.thenExecute(player -> helper.getLevel().getServer().getCommands().performPrefixedCommand(player.createCommandSourceStack(), "help"))
			.thenWaitUntil(() -> helper.assertTrue(TestRuntime.passed("server.command"), "script did not assert on server.command"))
			.thenExecute(() -> TestRuntime.verify("server.command"))
			.thenSucceed());
	}
}
