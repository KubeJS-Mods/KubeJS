package dev.latvian.kubejs.client;

import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * @author LatvianModder
 */
public class PackOverrides
{
	private static PackOverrides instance;
	private Minecraft minecraft;
	private File icon;
	private String title;

	public static PackOverrides get(Minecraft mc)
	{
		if (instance == null)
		{
			instance = new PackOverrides();
			instance.minecraft = mc;
			instance.refresh();
		}

		return instance;
	}

	public void refresh()
	{
		icon = null;
		title = "";

		File iconFile = new File(minecraft.gameDir, "kubejs/packicon.png");

		if (iconFile.exists() && iconFile.isFile())
		{
			icon = iconFile;
		}

		File titleFile = new File(minecraft.gameDir, "kubejs/packtitle.txt");

		if (titleFile.exists() && titleFile.isFile())
		{
			try
			{
				Files.lines(titleFile.toPath()).findFirst().ifPresent(s -> title = s.trim());
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String t)
	{
		title = t;
	}

	private boolean tempIconCancel = true;

	public boolean cancelIconUpdate()
	{
		if (tempIconCancel)
		{
			if (icon != null)
			{
				try (InputStream stream16 = Files.newInputStream(icon.toPath());
					 InputStream stream32 = Files.newInputStream(icon.toPath()))
				{
					tempIconCancel = false;
					minecraft.getMainWindow().setWindowIcon(stream16, stream32);
					tempIconCancel = true;
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}

				return true;
			}
			else
			{
				return false;
			}
		}

		return false;
	}
}