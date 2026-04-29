package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

@RemapPrefixForJS("kjs$")
public interface MessageSenderKJS {
	default Component kjs$getName() {
		throw new NoMixinException();
	}

	default Component kjs$getDisplayName() {
		return kjs$getName();
	}

	@Info(value = "Sends a message in chat to something.", params = {
		@Param(name = "message", value = "A text component. It may be a string, which will be implicitly wrapped into a text component."),
	})
	default void kjs$tell(Component message) {
		throw new NoMixinException();
	}

	default void kjs$setStatusMessage(Component message) {
	}

	@Info(value = "Runs the specified console command.", params = {
		@Param(name = "command", value = "The console command. Slash at the beginning is optional."),
	})
	default void kjs$runCommand(String command) {
	}

	@Info(value = "Runs the specified console command. The command won't output any logs in chat nor console.", params = {
		@Param(name = "command", value = "The console command. Slash at the beginning is optional."),
	})
	default void kjs$runCommandSilent(String command) {
		kjs$runCommand(command);
	}

	default void kjs$setActivePostShader(@Nullable Identifier id) {
	}
}