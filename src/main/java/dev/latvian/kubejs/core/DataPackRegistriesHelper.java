package dev.latvian.kubejs.core;

import dev.latvian.kubejs.KubeJSPaths;
import dev.latvian.kubejs.script.data.KubeJSResourcePack;
import dev.latvian.kubejs.server.ServerScriptManager;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackType;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class DataPackRegistriesHelper
{
	public static List<IResourcePack> getResourcePackListKJS(List<IResourcePack> list0)
	{
		if (Files.notExists(KubeJSPaths.DATA))
		{
			UtilsJS.tryIO(() -> Files.createDirectories(KubeJSPaths.DATA));
		}

		List<IResourcePack> list = new ArrayList<>();
		list.add(ServerScriptManager.instance.virtualDataPackLast);
		list.addAll(list0);
		list.add(new KubeJSResourcePack(ResourcePackType.SERVER_DATA));
		list.add(ServerScriptManager.instance.virtualDataPackFirst);
		return list;
	}
}
