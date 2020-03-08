package dev.latvian.kubejs.client;

import dev.latvian.kubejs.script.data.KubeJSResourcePack;
import net.minecraft.client.resources.ClientResourcePackInfo;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.data.PackMetadataSection;
import net.minecraft.util.text.StringTextComponent;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class KubeJSResourcePackFinder implements IPackFinder
{
	private final File folder;

	public KubeJSResourcePackFinder(File f)
	{
		folder = f;
	}

	@Override
	public <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> nameToPackMap, ResourcePackInfo.IFactory<T> packInfoFactory)
	{
		File assetsFolder = new File(folder, "assets");

		if (!assetsFolder.exists())
		{
			assetsFolder.mkdirs();

			File langFolder = new File(new File(assetsFolder, "modpack"), "lang");
			langFolder.mkdirs();

			try
			{
				try (PrintWriter initWriter = new PrintWriter(new FileWriter(new File(langFolder, "en_us.json"))))
				{
					initWriter.println("{");
					initWriter.println("\t\"modpack.example.translation_key\": \"Example Translation\"");
					initWriter.println("}");
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}

		KubeJSResourcePack pack = new KubeJSResourcePack(folder, ResourcePackType.CLIENT_RESOURCES);
		PackMetadataSection metadataSection = new PackMetadataSection(new StringTextComponent("./kubejs/assets/"), 5);
		ClientResourcePackInfo info = new ClientResourcePackInfo("kubejs:resource_pack", true, () -> pack, pack, metadataSection, ResourcePackInfo.Priority.TOP, false);
		nameToPackMap.put(info.getName(), (T) info);
	}
}