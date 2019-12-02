package dev.latvian.kubejs.client;

import dev.latvian.kubejs.MinecraftClass;
import dev.latvian.kubejs.player.ClientPlayerJS;
import dev.latvian.kubejs.world.ClientWorldJS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;

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
}