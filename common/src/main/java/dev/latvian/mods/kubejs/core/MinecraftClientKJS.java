package dev.latvian.mods.kubejs.core;

import com.mojang.blaze3d.platform.InputConstants;
import dev.latvian.mods.kubejs.client.ClientProperties;
import dev.latvian.mods.kubejs.level.ClientLevelJS;
import dev.latvian.mods.kubejs.player.ClientPlayerJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS("kjs$")
public interface MinecraftClientKJS {
	Minecraft kjs$self();

	@Nullable
	default ClientLevelJS kjs$getLevel() {
		return ClientLevelJS.getInstance();
	}

	@Nullable
	default ClientPlayerJS kjs$getPlayer() {
		if (ClientLevelJS.getInstance() == null) {
			return null;
		}

		return ClientLevelJS.getInstance().clientPlayerData.getPlayer();
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
		if (kjs$self().getCurrentServer() != null) {
			return kjs$self().getCurrentServer().name;
		}

		return "Singleplayer";
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
}
