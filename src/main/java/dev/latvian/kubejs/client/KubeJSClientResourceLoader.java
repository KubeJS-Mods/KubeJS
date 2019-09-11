package dev.latvian.kubejs.client;

import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.client.resources.IResourcePack;
import net.minecraftforge.fml.client.FMLClientHandler;

import java.io.File;
import java.util.List;

/**
 * @author LatvianModder
 */
public class KubeJSClientResourceLoader
{
	public static void init(File folder, ScriptManager scriptManager)
	{
		List<IResourcePack> packs = UtilsJS.getField(FMLClientHandler.class, "resourcePackList").get(FMLClientHandler.instance());

		if (packs != null)
		{
			File file = new File(folder, "resources");

			if (!file.exists())
			{
				file.mkdirs();
			}

			packs.add(new KubeJSResourcePack(file, scriptManager));
		}
	}
}