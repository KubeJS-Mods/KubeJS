package dev.latvian.kubejs.core;

import dev.latvian.kubejs.KubeJSPaths;
import dev.latvian.kubejs.script.data.KubeJSResourcePack;
import dev.latvian.kubejs.server.ServerScriptManager;
import dev.latvian.kubejs.util.UtilsJS;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;

/**
 * @author LatvianModder
 */
public class DataPackRegistriesHelper
{
	public static List<PackResources> getResourcePackListKJS(List<PackResources> list0)
	{
		if (Files.notExists(KubeJSPaths.DATA))
		{
			UtilsJS.tryIO(() -> Files.createDirectories(KubeJSPaths.DATA));
		}

		List<PackResources> list = new ArrayList<>();
		list.add(ServerScriptManager.instance.virtualDataPackLast);
		list.addAll(list0);
		list.add(new KubeJSResourcePack(PackType.SERVER_DATA));
		list.add(ServerScriptManager.instance.virtualDataPackFirst);
		return list;
	}
}
