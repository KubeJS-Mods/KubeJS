package dev.latvian.mods.kubejs.core;

import com.mojang.blaze3d.platform.InputConstants;
import dev.latvian.mods.kubejs.bindings.event.ItemEvents;
import dev.latvian.mods.kubejs.client.ClientProperties;
import dev.latvian.mods.kubejs.item.ItemClickedEventJS;
import dev.latvian.mods.kubejs.net.FirstClickMessage;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("resource")
@RemapPrefixForJS("kjs$")
public interface MinecraftClientKJS extends MinecraftEnvironmentKJS {
	default Minecraft kjs$self() {
		return (Minecraft) this;
	}

	@Override
	default Component kjs$getName() {
		return Component.literal(kjs$self().name());
	}

	@Override
	default void kjs$tell(Component message) {
		kjs$self().player.kjs$tell(message);
	}

	@Override
	default void kjs$setStatusMessage(Component message) {
		kjs$self().player.kjs$setStatusMessage(message);
	}

	@Override
	default int kjs$runCommand(String command) {
		kjs$self().player.connection.sendCommand(command);
		return 0;
	}

	@Override
	default int kjs$runCommandSilent(String command) {
		kjs$self().player.connection.sendCommand(command);
		return 0;
	}

	@Nullable
	default Screen kjs$getCurrentScreen() {
		return kjs$self().screen;
	}

	default void kjs$setCurrentScreen(Screen gui) {
		kjs$self().setScreen(gui);
	}

	default void kjs$setTitle(String t) {
		ClientProperties.get().title = t.trim();
		kjs$self().updateTitle();
	}

	default String kjs$getCurrentWorldName() {
		var server = kjs$self().getCurrentServer();
		return server == null ? "Singleplayer" : server.name;
	}

	default boolean kjs$isKeyDown(int key) {
		return InputConstants.isKeyDown(kjs$self().getWindow().getWindow(), key);
	}

	default boolean kjs$isShiftDown() {
		return Screen.hasShiftDown();
	}

	default boolean kjs$isCtrlDown() {
		return Screen.hasControlDown();
	}

	default boolean kjs$isAltDown() {
		return Screen.hasAltDown();
	}

	@HideFromJS
	default void kjs$startAttack0() {
		if (ItemEvents.FIRST_LEFT_CLICKED.hasListeners()) {
			var player = kjs$self().player;
			var stack = player.getItemInHand(InteractionHand.MAIN_HAND);
			ItemEvents.FIRST_LEFT_CLICKED.post(ScriptType.CLIENT, stack.getItem(), new ItemClickedEventJS(player, InteractionHand.MAIN_HAND, stack));
		}

		new FirstClickMessage(0).sendToServer();
	}

	@HideFromJS
	default void kjs$startUseItem0() {
		if (ItemEvents.FIRST_RIGHT_CLICKED.hasListeners()) {
			var player = kjs$self().player;

			for (var hand : InteractionHand.values()) {
				var stack = player.getItemInHand(hand);
				ItemEvents.FIRST_RIGHT_CLICKED.post(ScriptType.CLIENT, stack.getItem(), new ItemClickedEventJS(player, hand, stack));
			}
		}

		new FirstClickMessage(1).sendToServer();
	}
}
