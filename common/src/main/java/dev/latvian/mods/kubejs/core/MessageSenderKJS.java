package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.network.chat.Component;

/**
 * @author LatvianModder
 */
@RemapPrefixForJS("kjs$")
public interface MessageSenderKJS {
	default Component kjs$getName() {
		throw new NoMixinException();
	}

	default Component kjs$getDisplayName() {
		return kjs$getName();
	}

	default void kjs$tell(Component message) {
		throw new NoMixinException();
	}

	default void kjs$setStatusMessage(Component message) {
	}

	default int kjs$runCommand(String command) {
		throw new NoMixinException();
	}

	default int kjs$runCommandSilent(String command) {
		return kjs$runCommand(command);
	}
}