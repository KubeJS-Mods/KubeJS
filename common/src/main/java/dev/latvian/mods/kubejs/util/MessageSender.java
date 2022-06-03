package dev.latvian.mods.kubejs.util;

import net.minecraft.network.chat.Component;

/**
 * @author LatvianModder
 */
public interface MessageSender {
	Component getName();

	default Component getDisplayName() {
		return getName();
	}

	void tell(Component message);

	default void setStatusMessage(Component message) {
	}

	int runCommand(String command);

	default int runCommandSilent(String command) {
		return runCommand(command);
	}
}