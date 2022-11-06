package dev.latvian.mods.kubejs.core;

import com.mojang.blaze3d.platform.InputConstants;
import dev.latvian.mods.kubejs.bindings.event.ItemEvents;
import dev.latvian.mods.kubejs.client.ClientProperties;
import dev.latvian.mods.kubejs.item.ItemClickedEventJS;
import dev.latvian.mods.kubejs.net.FirstClickMessage;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("resource")
@RemapPrefixForJS("kjs$")
public interface MinecraftClientKJS {
	default Minecraft kjs$self() {
		return (Minecraft) this;
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
		var player = kjs$self().player;
		ItemEvents.CLIENT_LEFT_CLICKED.post(player.getItemInHand(InteractionHand.MAIN_HAND).getItem(), new ItemClickedEventJS(player, InteractionHand.MAIN_HAND));
		new FirstClickMessage(0).sendToServer();
	}

	@HideFromJS
	default void kjs$startUseItem0() {
		var player = kjs$self().player;

		for (var hand : InteractionHand.values()) {
			ItemEvents.CLIENT_RIGHT_CLICKED.post(player.getItemInHand(hand).getItem(), new ItemClickedEventJS(player, hand));
		}

		new FirstClickMessage(1).sendToServer();
	}
}
