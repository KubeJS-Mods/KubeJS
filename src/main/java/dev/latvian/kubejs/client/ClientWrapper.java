package dev.latvian.kubejs.client;

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
	public Minecraft getMinecraft()
	{
		return Minecraft.getInstance();
	}

	public ClientWorldJS getWorld()
	{
		return ClientWorldJS.get();
	}

	public ClientPlayerJS getPlayer()
	{
		return getWorld().clientPlayerData.getPlayer();
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