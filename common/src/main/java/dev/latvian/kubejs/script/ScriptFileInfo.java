package dev.latvian.kubejs.script;

import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author LatvianModder
 */
public class ScriptFileInfo
{
	private static final Pattern FILE_FIXER = Pattern.compile("[^\\w.\\/]");

	public final ScriptPackInfo pack;
	public final String file;
	public final ResourceLocation id;
	public final String location;
	private final Map<String, String> properties;
	private int priority;
	private boolean ignored;

	public ScriptFileInfo(ScriptPackInfo p, String f)
	{
		pack = p;
		file = f;
		id = new ResourceLocation(pack.namespace, FILE_FIXER.matcher(pack.pathStart + file).replaceAll("_").toLowerCase());
		location = UtilsJS.getID(pack.namespace + ":" + pack.pathStart + file);
		properties = new HashMap<>();
		priority = 0;
		ignored = false;
	}

	@Nullable
	public Throwable preload(ScriptSource source)
	{
		properties.clear();
		priority = 0;
		ignored = false;

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(source.createStream(this), StandardCharsets.UTF_8)))
		{
			String line;

			while ((line = reader.readLine()) != null)
			{
				line = line.trim();

				if (line.startsWith("//"))
				{
					String[] s = line.substring(2).split(":", 2);

					if (s.length == 2)
					{
						properties.put(s[0].trim().toLowerCase(), s[1].trim());
					}
				}
				else
				{
					break;
				}
			}

			priority = Integer.parseInt(getProperty("priority", "0"));
			ignored = getProperty("ignored", "false").equals("true");
			return null;
		}
		catch (Throwable ex)
		{
			return ex;
		}
	}

	public String getProperty(String s, String def)
	{
		return properties.getOrDefault(s, def);
	}

	public int getPriority()
	{
		return priority;
	}

	public boolean isIgnored()
	{
		return ignored;
	}
}