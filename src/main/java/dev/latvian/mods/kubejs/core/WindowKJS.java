package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.client.ClientProperties;
import dev.latvian.mods.kubejs.script.data.GeneratedData;
import net.minecraft.server.packs.resources.IoSupplier;

import javax.imageio.ImageIO;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.function.Supplier;

public interface WindowKJS {
	record KJSScaledIconProvider(BufferedImage original, int target) implements Supplier<byte[]> {
		@Override
		public byte[] get() {
			try {
				var out = new ByteArrayOutputStream();

				if (original.getWidth() == target && original.getHeight() == target) {
					ImageIO.write(original, "png", out);
				} else {
					var img = new BufferedImage(target, target, BufferedImage.TYPE_INT_ARGB);
					var g = img.createGraphics();

					if (ClientProperties.get().blurScaledPackIcon) {
						g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
					} else {
						g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
					}

					g.drawImage(original, 0, 0, target, target, null);
					g.dispose();
					ImageIO.write(img, "png", out);
				}

				return out.toByteArray();
			} catch (Exception ex) {
				throw new IllegalStateException(original.toString(), ex);
			}
		}
	}

	default List<IoSupplier<InputStream>> kjs$loadIcons(List<IoSupplier<InputStream>> original) throws IOException {
		if (Files.exists(KubeJSPaths.PACKICON)) {
			try (var in = Files.newInputStream(KubeJSPaths.PACKICON)) {
				var img = ImageIO.read(in);

				return List.of(
					new GeneratedData(KubeJS.id("icon_16x.png"), new KJSScaledIconProvider(img, 16)),
					new GeneratedData(KubeJS.id("icon_24x.png"), new KJSScaledIconProvider(img, 24)),
					new GeneratedData(KubeJS.id("icon_32x.png"), new KJSScaledIconProvider(img, 32)),
					new GeneratedData(KubeJS.id("icon_48x.png"), new KJSScaledIconProvider(img, 48)),
					new GeneratedData(KubeJS.id("icon_128.png"), new KJSScaledIconProvider(img, 128)),
					new GeneratedData(KubeJS.id("icon_256x.png"), new KJSScaledIconProvider(img, 256))
				);
			}
		}

		return original;
	}
}
