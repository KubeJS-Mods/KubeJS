package dev.latvian.mods.kubejs.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.latvian.mods.kubejs.level.ClientLevelJS;
import dev.latvian.mods.kubejs.player.ClientPlayerJS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public class ClientWrapper {
	public Minecraft getMinecraft() {
		return Minecraft.getInstance();
	}

	@Nullable
	@Deprecated(forRemoval = true)
	@ApiStatus.ScheduledForRemoval(inVersion = "4.2")
	public final ClientLevelJS getWorld() {
		return getLevel();
	}

	@Nullable
	public ClientLevelJS getLevel() {
		return ClientLevelJS.getInstance();
	}

	@Nullable
	public ClientPlayerJS getPlayer() {
		if (ClientLevelJS.getInstance() == null) {
			return null;
		}

		return ClientLevelJS.getInstance().clientPlayerData.getPlayer();
	}

	@Nullable
	public Screen getCurrentGui() {
		return getMinecraft().screen;
	}

	public void setCurrentGui(Screen gui) {
		getMinecraft().setScreen(gui);
	}

	public void setTitle(String t) {
		ClientProperties.get().title = t.trim();
		getMinecraft().updateTitle();
	}

	public String getCurrentWorldName() {
		if (getMinecraft().getCurrentServer() != null) {
			return getMinecraft().getCurrentServer().name;
		}

		return "Singleplayer";
	}

	public boolean isKeyDown(int key) {
		return InputConstants.isKeyDown(getMinecraft().getWindow().getWindow(), key);
	}
}