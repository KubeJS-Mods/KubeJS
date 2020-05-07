package dev.latvian.kubejs.client;

import dev.latvian.kubejs.MinecraftClass;
import dev.latvian.kubejs.player.ClientPlayerJS;
import dev.latvian.kubejs.world.ClientWorldJS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputMappings;

import javax.annotation.Nullable;

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
		return getMinecraft().currentScreen;
	}

	public void setCurrentGui(Screen gui)
	{
		getMinecraft().displayGuiScreen(gui);
	}

	public void setTitle(String t)
	{
		ClientProperties.get().title = t.trim();
		getMinecraft().func_230150_b_();
	}

	public String getCurrentWorldName()
	{
		if (getMinecraft().getCurrentServerData() != null)
		{
			return getMinecraft().getCurrentServerData().serverName;
		}

		return "Singleplayer";
	}

	public boolean isKeyDown(int key)
	{
		return InputMappings.isKeyDown(getMinecraft().getMainWindow().getHandle(), key);
	}
}