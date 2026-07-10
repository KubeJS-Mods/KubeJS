package dev.latvian.mods.kubejs.testmod;

import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.testframework.conf.FrameworkConfiguration;

/// Entry point for the {@code testmod} game-test mod. Stands up a NeoForge test framework that collects
/// the mod's annotated tests and registers them on the mod bus. Loaded only by the headless
/// {@code runGametest} ({@code gameTestServer}) run.
@Mod("testmod")
public class KubeJSGameTests {
	public static final String MOD_ID = "testmod";

	public KubeJSGameTests(IEventBus modBus, ModContainer container) {
		var framework = FrameworkConfiguration
			.builder(Identifier.fromNamespaceAndPath(MOD_ID, "tests"))
			.build()
			.create();

		framework.init(modBus, container);
	}
}
