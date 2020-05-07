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
	public static int backgroundColor = 0xFFFFFF;
	public static int barColor = 0xE22837;
	public static int barBackgroundColor = 0xFFFFFF;
	public static int barBorderColor = 0x000000;
	public static float[] fmlMemoryColor = null;
	public static float[] fmlLogColor = null;

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

	public void setBackgroundColor(int color)
	{
		backgroundColor = color;
	}

	public void setBarColor(int color)
	{
		barColor = color;
	}

	public void setBarBackgroundColor(int color)
	{
		barBackgroundColor = color;
	}

	public void setBarBorderColor(int color)
	{
		barBorderColor = color;
	}

	public void setFmlMemoryColor(int color)
	{
		fmlMemoryColor = new float[3];
		fmlMemoryColor[0] = ((color >> 16) & 0xFF) / 255F;
		fmlMemoryColor[1] = ((color >> 8) & 0xFF) / 255F;
		fmlMemoryColor[2] = ((color >> 0) & 0xFF) / 255F;
	}

	public void setFmlLogColor(int color)
	{
		fmlLogColor = new float[3];
		fmlLogColor[0] = ((color >> 16) & 0xFF) / 255F;
		fmlLogColor[1] = ((color >> 8) & 0xFF) / 255F;
		fmlLogColor[2] = ((color >> 0) & 0xFF) / 255F;
	}
}