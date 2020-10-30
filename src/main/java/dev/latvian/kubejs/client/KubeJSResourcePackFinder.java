package dev.latvian.kubejs.client;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSPaths;
import dev.latvian.kubejs.script.data.KubeJSResourcePack;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.data.PackMetadataSection;
import net.minecraft.util.text.StringTextComponent;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class KubeJSResourcePackFinder implements IPackFinder
{
	@Override
	public void findPacks(Consumer<ResourcePackInfo> nameToPackMap, ResourcePackInfo.IFactory packInfoFactory)
	{
		if (Files.notExists(KubeJSPaths.ASSETS))
		{
			UtilsJS.tryIO(() -> Files.createDirectories(KubeJSPaths.ASSETS));
			UtilsJS.tryIO(() -> Files.createDirectories(KubeJSPaths.ASSETS.resolve("kubejs/textures/block")));
			UtilsJS.tryIO(() -> Files.createDirectories(KubeJSPaths.ASSETS.resolve("kubejs/textures/item")));

			try (InputStream in = KubeJS.class.getResourceAsStream("/data/kubejs/example_block_texture.png");
				 OutputStream out = Files.newOutputStream(KubeJSPaths.ASSETS.resolve("kubejs/textures/block/example_block.png")))
			{
				out.write(IOUtils.toByteArray(in));
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}

			try (InputStream in = KubeJS.class.getResourceAsStream("/data/kubejs/example_item_texture.png");
				 OutputStream out = Files.newOutputStream(KubeJSPaths.ASSETS.resolve("kubejs/textures/item/example_item.png")))
			{
				out.write(IOUtils.toByteArray(in));
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}

		KubeJSResourcePack pack = new KubeJSResourcePack(ResourcePackType.CLIENT_RESOURCES);
		PackMetadataSection metadataSection = new PackMetadataSection(new StringTextComponent("./kubejs/assets/"), 6);
		nameToPackMap.accept(new ResourcePackInfo("kubejs:resource_pack", true, () -> pack, pack, metadataSection, ResourcePackInfo.Priority.TOP, IPackNameDecorator.BUILTIN, false));
	}
}