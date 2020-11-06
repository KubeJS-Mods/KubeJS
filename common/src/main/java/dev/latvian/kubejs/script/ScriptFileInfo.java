package dev.latvian.kubejs.script;

import org.jetbrains.annotations.Nullable;
import net.minecraft.resources.ResourceLocation;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class ScriptFileInfo
{
	public final ScriptPackInfo pack;
	public final String file;
	public final ResourceLocation location;
	private final Map<String, String> properties;
	private int priority;

	public ScriptFileInfo(ScriptPackInfo p, String f)
	{
		pack = p;
		file = f;
		location = new ResourceLocation(pack.namespace, pack.pathStart + file);
		properties = new HashMap<>();
		priority = 0;
	}

	@Nullable
	public Throwable preload(ScriptSource source)
	{
		properties.clear();

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
}