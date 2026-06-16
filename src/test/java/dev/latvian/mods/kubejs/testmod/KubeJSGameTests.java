package dev.latvian.mods.kubejs.testmod;

import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;

/// Entry point for the {@code testmod} game-test mod, registering KubeJS' game tests on the
/// mod bus. Loaded only by the headless {@code runGametest} ({@code gameTestServer}) run.
@Mod("testmod")
public class KubeJSGameTests {
	public static final String MOD_ID = "testmod";

	public KubeJSGameTests(IEventBus modBus) {
		modBus.addListener(this::registerGameTests);
	}

	private void registerGameTests(RegisterGameTestsEvent event) {
		Holder<TestEnvironmentDefinition<?>> environment = event.registerEnvironment(id("default"));

		TestData<Holder<TestEnvironmentDefinition<?>>> data = new TestData<>(
			environment,
			Identifier.withDefaultNamespace("empty"),
			100,
			0,
			true
		);

		event.registerTest(id("block_broken_dirt"), new BlockBrokenGameTest(data));
	}

	private static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
