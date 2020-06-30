package dev.latvian.kubejs.script.data;

import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.data.PackMetadataSection;
import net.minecraft.util.text.StringTextComponent;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class KubeJSDataPackFinder implements IPackFinder
{
	private final File folder;

	public KubeJSDataPackFinder(File f)
	{
		folder = f;
	}

	@Override
	public <T extends ResourcePackInfo> void func_230230_a_(Consumer<T> nameToPackMap, ResourcePackInfo.IFactory<T> packInfoFactory)
	{
		File dataFolder = new File(folder, "data");

		if (!dataFolder.exists())
		{
			File scriptsFolder = new File(new File(dataFolder, "modpack"), "kubejs");
			scriptsFolder.mkdirs();

			try
			{
				try (PrintWriter scriptsJsonWriter = new PrintWriter(new FileWriter(new File(scriptsFolder, "scripts.json"))))
				{
					scriptsJsonWriter.println("{");
					scriptsJsonWriter.println("	\"scripts\": [");
					scriptsJsonWriter.println("		{\"file\": \"example.js\"}");
					scriptsJsonWriter.println("	]");
					scriptsJsonWriter.println("}");
				}

				try (PrintWriter exampleJsWriter = new PrintWriter(new FileWriter(new File(scriptsFolder, "example.js"))))
				{
					exampleJsWriter.println("console.info('Hello, World! (You will see this line every time you start server or run /reload)')");
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}

		KubeJSResourcePack dataPack = new KubeJSResourcePack(folder, ResourcePackType.SERVER_DATA);
		PackMetadataSection dataPackMetadata = new PackMetadataSection(new StringTextComponent("./kubejs/data/"), 5);
		nameToPackMap.accept((T) new ResourcePackInfo("kubejs:data_pack", true, () -> dataPack, dataPack, dataPackMetadata, ResourcePackInfo.Priority.TOP, IPackNameDecorator.field_232626_b_, true));
	}
}