package dev.latvian.mods.kubejs.unittest;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/// JUnit 5 extension that boots Minecraft's registries once (process-wide) so unit tests can exercise
/// registry-backed script API - wrappers, builders, codecs - without standing up a game server.
/// Apply with {@code @ExtendWith(BootstrapExtension.class)}.
public class BootstrapExtension implements BeforeAllCallback {
	private static volatile boolean bootstrapped = false;

	@Override
	public void beforeAll(ExtensionContext context) {
		if (!bootstrapped) {
			synchronized (BootstrapExtension.class) {
				if (!bootstrapped) {
					SharedConstants.tryDetectVersion();
					Bootstrap.bootStrap();
					bootstrapped = true;
				}
			}
		}
	}
}
