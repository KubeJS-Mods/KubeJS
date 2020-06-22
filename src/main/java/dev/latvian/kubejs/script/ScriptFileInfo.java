package dev.latvian.kubejs.script;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;

import javax.annotation.Nullable;
import java.io.BufferedReader;
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
	private Dist side;

	public ScriptFileInfo(ScriptPackInfo p, String f)
	{
		pack = p;
		file = f;
		location = new ResourceLocation(pack.namespace, pack.pathStart + file);
		properties = new HashMap<>();
		priority = 0;
		side = null;
	}

	@Nullable
	public Throwable preload(ScriptSource source)
	{
		properties.clear();

		try (BufferedReader reader = new BufferedReader(source.createReader(this)))
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

			switch (getProperty("side", "common").toLowerCase())
			{
				case "client":
					side = Dist.CLIENT;
					break;
				case "server":
					side = Dist.DEDICATED_SERVER;
					break;
				default:
					side = null;
			}

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

	public boolean shouldLoad(Dist dist)
	{
		return side == null || side == dist;
	}
}