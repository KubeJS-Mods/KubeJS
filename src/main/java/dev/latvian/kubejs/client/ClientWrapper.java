package dev.latvian.kubejs.client;

import dev.latvian.kubejs.player.ClientPlayerJS;
import dev.latvian.kubejs.world.ClientWorldJS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class ClientWrapper
{
	public Minecraft getMinecraft()
	{
		return Minecraft.getMinecraft();
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
	public GuiScreen getCurrentGui()
	{
		return getMinecraft().currentScreen;
	}

	public void setCurrentGui(GuiScreen gui)
	{
		getMinecraft().displayGuiScreen(gui);
	}
}