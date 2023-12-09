package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.network.chat.Component;

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

	default void kjs$runCommand(String command) {
	}

	default void kjs$runCommandSilent(String command) {
		kjs$runCommand(command);
	}
}