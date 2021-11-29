package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.server.packs.repository.Pack;
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

		// Moved pack to mixin
	}
}