package dev.latvian.kubejs.core;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.script.data.KubeJSResourcePack;
import dev.latvian.kubejs.server.ServerScriptManager;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class DataPackRegistriesHelper
{
	public static List<IResourcePack> getResourcePackListKJS(List<IResourcePack> list0)
	{
		List<IResourcePack> list = new ArrayList<>();
		list.add(ServerScriptManager.instance.virtualDataPackLast);
		list.addAll(list0);
		list.add(new KubeJSResourcePack(KubeJS.getGameDirectory().resolve("kubejs").toFile(), ResourcePackType.SERVER_DATA));
		list.add(ServerScriptManager.instance.virtualDataPackFirst);

		System.out.println(list);
		// net.minecraft.resources.VanillaPack@3e7fc07e, net.minecraftforge.fml.packs.ModFileResourcePack@29ebaf2f, net.minecraftforge.fml.packs.ModFileResourcePack@1a61f634
		return list;
	}
}
