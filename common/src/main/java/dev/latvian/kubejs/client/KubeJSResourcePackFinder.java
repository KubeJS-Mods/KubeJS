package dev.latvian.kubejs.client;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSPaths;
import dev.latvian.kubejs.script.data.KubeJSResourcePack;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class KubeJSResourcePackFinder implements RepositorySource {
	@Override
	public void loadPacks(Consumer<Pack> nameToPackMap, Pack.PackConstructor packInfoFactory) {
		if (Files.notExists(KubeJSPaths.ASSETS)) {
			UtilsJS.tryIO(() -> Files.createDirectories(KubeJSPaths.ASSETS));
			UtilsJS.tryIO(() -> Files.createDirectories(KubeJSPaths.ASSETS.resolve("kubejs/textures/block")));
			UtilsJS.tryIO(() -> Files.createDirectories(KubeJSPaths.ASSETS.resolve("kubejs/textures/item")));

			try (InputStream in = KubeJS.class.getResourceAsStream("/data/kubejs/example_block_texture.png");
				 OutputStream out = Files.newOutputStream(KubeJSPaths.ASSETS.resolve("kubejs/textures/block/example_block.png"))) {
				out.write(IOUtils.toByteArray(in));
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			try (InputStream in = KubeJS.class.getResourceAsStream("/data/kubejs/example_item_texture.png");
				 OutputStream out = Files.newOutputStream(KubeJSPaths.ASSETS.resolve("kubejs/textures/item/example_item.png"))) {
				out.write(IOUtils.toByteArray(in));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		KubeJSResourcePack pack = new KubeJSResourcePack(PackType.CLIENT_RESOURCES);
		PackMetadataSection metadataSection = new PackMetadataSection(new TextComponent("./kubejs/assets/"), 6);
		nameToPackMap.accept(new Pack("kubejs:resource_pack", true, () -> pack, pack, metadataSection, Pack.Position.TOP, PackSource.BUILT_IN));
	}
}