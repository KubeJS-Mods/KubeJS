package dev.latvian.kubejs;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * @author LatvianModder
 */
public class CommonProperties
{
	private static CommonProperties instance;

	public static CommonProperties get()
	{
		if (instance == null)
		{
			instance = new CommonProperties();
		}

		return instance;
	}

	private final Properties properties;
	private boolean writeProperties;

	public boolean enableES6;

	private CommonProperties()
	{
		properties = new Properties();

		try
		{
			Path propertiesFile = KubeJSPaths.CONFIG.resolve("common.properties");
			writeProperties = false;

			if (Files.exists(propertiesFile))
			{
				try (Reader reader = Files.newBufferedReader(propertiesFile))
				{
					properties.load(reader);
				}
			}
			else
			{
				writeProperties = true;
			}

			enableES6 = get("enableES6", true);

			if (writeProperties)
			{
				try (Writer writer = Files.newBufferedWriter(propertiesFile))
				{
					properties.store(writer, "KubeJS Common Properties\nIt's recommended to disable ES6 if you want to improve loading times and don't care about lambdas and other ES6 JavaScript features.");
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		KubeJS.LOGGER.info("Loaded common.properties");
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
}