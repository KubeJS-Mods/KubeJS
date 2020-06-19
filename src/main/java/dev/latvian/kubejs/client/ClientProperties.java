package dev.latvian.kubejs.client;

import dev.latvian.kubejs.KubeJS;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.loading.FMLPaths;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * @author LatvianModder
 */
public class ClientProperties
{
	private static ClientProperties instance;

	public static ClientProperties get()
	{
		if (instance == null)
		{
			instance = new ClientProperties();
		}

		return instance;
	}

	private final Properties properties;
	private boolean writeProperties;
	private Path icon;
	private boolean tempIconCancel = true;

	public String title;
	public boolean showTagNames;
	public boolean disableRecipeBook;
	public boolean exportAtlases;
	public int backgroundColor;
	public float[] backgroundColor3f;
	public int barColor;
	public int barBackgroundColor;
	public int barBorderColor;
	public float[] fmlMemoryColor;
	public float[] fmlLogColor;

	private ClientProperties()
	{
		properties = new Properties();

		try
		{
			Path folder = FMLPaths.GAMEDIR.get().resolve("kubejs");

			if (Files.notExists(folder))
			{
				Files.createDirectories(folder);
			}

			Path p = folder.resolve("client.properties");
			writeProperties = false;

			if (Files.exists(p))
			{
				try (Reader reader = Files.newBufferedReader(p))
				{
					properties.load(reader);
				}
			}
			else
			{
				writeProperties = true;
			}

			Path titleFile = folder.resolve("packtitle.txt");

			if (Files.exists(titleFile))
			{
				try
				{
					Files.lines(titleFile).findFirst().ifPresent(s -> properties.setProperty("title", s.trim()));
					Files.delete(titleFile);
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}

			title = get("title", "");
			showTagNames = get("showTagNames", true);
			disableRecipeBook = get("disableRecipeBook", false);
			exportAtlases = get("exportAtlases", false);
			backgroundColor = getColor("backgroundColor", 0xFFFFFF);
			backgroundColor3f = getFloatColor(backgroundColor);
			barColor = getColor("barColor", 0xE22837);
			barBackgroundColor = getColor("barBackgroundColor", 0xFFFFFF);
			barBorderColor = getColor("barBorderColor", 0x000000);
			fmlMemoryColor = getFloatColor("fmlMemoryColor");
			fmlLogColor = getFloatColor("fmlLogColor");

			Path iconFile = folder.resolve("packicon.png");

			if (Files.exists(iconFile))
			{
				icon = iconFile;
			}

			if (writeProperties)
			{
				try (Writer writer = Files.newBufferedWriter(p))
				{
					properties.store(writer, "KubeJS Client Properties");
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		KubeJS.LOGGER.info("Loaded kubejs/client.properties");
	}

	private String get(String key, String def)
	{
		String s = properties.getProperty(key);

		if (s == null)
		{
			properties.setProperty(key, def);
			writeProperties = true;
			return def;
		}

		return s;
	}

	private boolean get(String key, boolean def)
	{
		return get(key, def ? "true" : "false").equals("true");
	}

	private int getColor(String key, int def)
	{
		String s = get(key, String.format("#%06X", def & 0xFFFFFF));

		if (s.isEmpty() || s.equals("default"))
		{
			return def;
		}

		try
		{
			return 0xFFFFFF & Integer.decode(s.startsWith("#") ? s : ("#" + s));
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return def;
		}
	}

	private float[] getFloatColor(int color)
	{
		float[] c = new float[3];
		c[0] = ((color >> 16) & 0xFF) / 255F;
		c[1] = ((color >> 8) & 0xFF) / 255F;
		c[2] = ((color >> 0) & 0xFF) / 255F;
		return c;
	}

	@Nullable
	private float[] getFloatColor(String key)
	{
		String s = get(key, "default");

		if (s.isEmpty() || s.equals("default"))
		{
			return null;
		}

		try
		{
			return getFloatColor(Integer.decode(s));
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public boolean cancelIconUpdate()
	{
		if (tempIconCancel)
		{
			if (icon != null)
			{
				try (InputStream stream16 = Files.newInputStream(icon);
					 InputStream stream32 = Files.newInputStream(icon))
				{
					tempIconCancel = false;
					Minecraft.getInstance().getMainWindow().setWindowIcon(stream16, stream32);
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