package dev.latvian.kubejs.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.latvian.kubejs.docs.MinecraftClass;
import dev.latvian.kubejs.player.ClientPlayerJS;
import dev.latvian.kubejs.world.ClientWorldJS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public class ClientWrapper
{
	@MinecraftClass
	public Minecraft getMinecraft()
	{
		return Minecraft.getInstance();
	}

	@Nullable
	public ClientWorldJS getWorld()
	{
		return ClientWorldJS.instance;
	}

	@Nullable
	public ClientPlayerJS getPlayer()
	{
		if (ClientWorldJS.instance == null)
		{
			return null;
		}

		return ClientWorldJS.instance.clientPlayerData.getPlayer();
	}

	@Nullable
	public Screen getCurrentGui()
	{
		return getMinecraft().screen;
	}

	public void setCurrentGui(Screen gui)
	{
		getMinecraft().setScreen(gui);
	}

	public void setTitle(String t)
	{
		ClientProperties.get().title = t.trim();
		getMinecraft().updateTitle();
	}

	public String getCurrentWorldName()
	{
		if (getMinecraft().getCurrentServer() != null)
		{
			return getMinecraft().getCurrentServer().name;
		}

		return "Singleplayer";
	}

	public boolean isKeyDown(int key)
	{
		return InputConstants.isKeyDown(getMinecraft().getWindow().getWindow(), key);
	}
}