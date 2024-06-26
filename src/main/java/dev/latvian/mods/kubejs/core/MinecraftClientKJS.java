package dev.latvian.mods.kubejs.core;

import com.mojang.blaze3d.platform.InputConstants;
import dev.latvian.mods.kubejs.bindings.event.ItemEvents;
import dev.latvian.mods.kubejs.client.ClientProperties;
import dev.latvian.mods.kubejs.item.ItemClickedKubeEvent;
import dev.latvian.mods.kubejs.net.FirstClickPayload;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

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
	default void kjs$runCommand(String command) {
		kjs$self().player.connection.sendCommand(command);
	}

	@Override
	default void kjs$runCommandSilent(String command) {
		kjs$self().player.connection.sendCommand(command);
	}

	@Nullable
	default Screen kjs$getCurrentScreen() {
		return kjs$self().screen;
	}

	default void kjs$setCurrentScreen(Screen gui) {
		kjs$self().setScreen(gui);
	}

	default void kjs$setTitle(String t) {
		ClientProperties.get().windowTitle = t.trim();
		kjs$self().updateTitle();
	}

	default String kjs$getCurrentWorldName() {
		var server = kjs$self().getCurrentServer();
		return server == null ? "Singleplayer" : server.name;
	}

	default boolean kjs$isKeyDown(int key) {
		return InputConstants.isKeyDown(kjs$self().getWindow().getWindow(), key);
	}

	default boolean kjs$isKeyMappingDown(KeyMapping key) {
		if (!key.isUnbound() && key.isConflictContextAndModifierActive()) {
			if (key.getKey().getType() == InputConstants.Type.KEYSYM) {
				return kjs$isKeyDown(key.getKey().getValue());
			} else if (key.getKey().getType() == InputConstants.Type.MOUSE) {
				return GLFW.glfwGetMouseButton(kjs$self().getWindow().getWindow(), key.getKey().getValue()) == GLFW.GLFW_TRUE;
			}
		}

		return false;
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
			var key = stack.getItem().kjs$getRegistryKey();

			if (ItemEvents.FIRST_LEFT_CLICKED.hasListeners(key)) {
				ItemEvents.FIRST_LEFT_CLICKED.post(ScriptType.CLIENT, key, new ItemClickedKubeEvent(player, InteractionHand.MAIN_HAND, stack));
			}
		}

		PacketDistributor.sendToServer(new FirstClickPayload(0));
	}

	@HideFromJS
	default void kjs$startUseItem0() {
		if (ItemEvents.FIRST_RIGHT_CLICKED.hasListeners()) {
			var player = kjs$self().player;

			for (var hand : InteractionHand.values()) {
				var stack = player.getItemInHand(hand);
				var key = stack.getItem().kjs$getRegistryKey();

				if (ItemEvents.FIRST_RIGHT_CLICKED.hasListeners(key)) {
					ItemEvents.FIRST_RIGHT_CLICKED.post(ScriptType.CLIENT, key, new ItemClickedKubeEvent(player, hand, stack));
				}
			}
		}

		PacketDistributor.sendToServer(new FirstClickPayload(1));
	}

	@HideFromJS
	default void kjs$afterResourcesLoaded(boolean reload) {
		ConsoleJS.CLIENT.stopCapturingErrors();
		ConsoleJS.CLIENT.info("Client resource reload complete!");
	}
}
