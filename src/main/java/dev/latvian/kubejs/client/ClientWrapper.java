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
	public static boolean showTagNames = true;
	public static boolean disableRecipeBook = false;
	public static boolean exportAtlases = false;

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
		PackOverrides.get(getMinecraft()).setTitle(t);
		getMinecraft().func_230150_b_();
	}

	public void setIcon(String icon16, String icon32)
	{
	}

	public void setIcon(String icon)
	{
	}

	public String getCurrentWorldName()
	{
		if (getMinecraft().getCurrentServerData() != null)
		{
			return getMinecraft().getCurrentServerData().serverName;
		}

		return "Singleplayer";
	}

	public void setShowTagNames(boolean v)
	{
		showTagNames = v;
	}

	public void setDisableRecipeBook(boolean v)
	{
		disableRecipeBook = v;
	}

	public void setExportAtlases(boolean v)
	{
		exportAtlases = v;
	}

	public boolean isKeyDown(int key)
	{
		return InputMappings.isKeyDown(getMinecraft().getMainWindow().getHandle(), key);
	}
}