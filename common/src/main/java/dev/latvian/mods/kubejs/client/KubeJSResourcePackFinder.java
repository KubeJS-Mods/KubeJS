package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPaths;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.RepositorySource;

import java.nio.file.Files;
import java.util.function.Consumer;

public class KubeJSResourcePackFinder implements RepositorySource {
	@Override
	public void loadPacks(Consumer<Pack> nameToPackMap) {
		if (KubeJSPaths.FIRST_RUN.getValue()) {
			var blockTextures = KubeJSPaths.dir(KubeJSPaths.ASSETS.resolve("kubejs/textures/block"));
			var itemTextures = KubeJSPaths.dir(KubeJSPaths.ASSETS.resolve("kubejs/textures/item"));

			try (var in = Files.newInputStream(KubeJS.thisMod.findResource("data", "kubejs", "example_block_texture.png").get());
			     var out = Files.newOutputStream(blockTextures.resolve("example_block.png"))) {
				in.transferTo(out);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			try (var in = Files.newInputStream(KubeJS.thisMod.findResource("data", "kubejs", "example_item_texture.png").get());
			     var out = Files.newOutputStream(itemTextures.resolve("example_item.png"))) {
				in.transferTo(out);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}