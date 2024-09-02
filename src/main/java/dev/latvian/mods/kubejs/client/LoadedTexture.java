package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.color.KubeColor;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

public class LoadedTexture {
	public static final LoadedTexture EMPTY = new LoadedTexture(0, 0, new int[0], null);

	public static LoadedTexture load(ResourceLocation id) {
		try {
			var path = KubeJSPaths.ASSETS.resolve(id.getNamespace() + "/textures/" + id.getPath() + ".png");

			if (Files.exists(path)) {
				try (var in = new BufferedInputStream(Files.newInputStream(path))) {
					var metaPath = KubeJSPaths.ASSETS.resolve(id.getNamespace() + "/textures/" + id.getPath() + ".png.mcmeta");
					return new LoadedTexture(ImageIO.read(in), Files.exists(metaPath) ? Files.readAllBytes(metaPath) : null);
				}
			} else if (id.getNamespace().equals(KubeJS.MOD_ID)) {
				var path1 = KubeJS.thisMod.getModInfo().getOwningFile().getFile().findResource("assets", "kubejs", "textures", id.getPath() + ".png");

				if (Files.exists(path1)) {
					try (var in = new BufferedInputStream(Files.newInputStream(path1))) {
						var metaPath = KubeJS.thisMod.getModInfo().getOwningFile().getFile().findResource("assets", "kubejs", "textures", id.getPath() + ".png.mcmeta");
						return new LoadedTexture(ImageIO.read(in), Files.exists(metaPath) ? Files.readAllBytes(metaPath) : null);
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return EMPTY;
	}

	public final int width;
	public final int height;
	public final int[] pixels;
	public final byte[] mcmeta;

	public LoadedTexture(int width, int height, int[] pixels, @Nullable byte[] mcmeta) {
		this.width = width;
		this.height = height;
		this.pixels = pixels;
		this.mcmeta = mcmeta;
	}

	public LoadedTexture(BufferedImage img, @Nullable byte[] mcmeta) {
		this.width = img.getWidth();
		this.height = img.getHeight();
		this.pixels = new int[width * height];
		img.getRGB(0, 0, width, height, pixels, 0, width);
		this.mcmeta = mcmeta;
	}

	public byte[] toBytes() {
		if (width == 0 || height == 0) {
			return new byte[0];
		}

		var img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		img.setRGB(0, 0, width, height, pixels, 0, width);

		var out = new ByteArrayOutputStream();

		try {
			ImageIO.write(img, "png", out);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		return out.toByteArray();
	}

	public LoadedTexture copy() {
		return new LoadedTexture(width, height, pixels.clone(), mcmeta);
	}

	public LoadedTexture remap(Map<KubeColor, KubeColor> remap) {
		if (remap.isEmpty()) {
			return this;
		}

		var colorMap = new Int2IntArrayMap(remap.size());

		for (var entry : remap.entrySet()) {
			var k = entry.getKey();
			var v = entry.getValue();
			colorMap.put(k.kjs$getARGB(), v.kjs$getARGB());
		}

		int[] result = new int[pixels.length];

		for (int i = 0; i < pixels.length; i++) {
			result[i] = ((pixels[i] & 0xFF000000) == 0) ? 0 : colorMap.getOrDefault(pixels[i], pixels[i]);
		}

		return new LoadedTexture(width, height, result, mcmeta);
	}

	public LoadedTexture resize(int newWidth, int newHeight) {
		if (width == newWidth && height == newHeight) {
			return this;
		}

		var source = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		source.setRGB(0, 0, width, height, pixels, 0, width);

		BufferedImage dst = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D bg = dst.createGraphics();
		bg.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		float sx = (float) newWidth / (float) width;
		float sy = (float) newHeight / (float) height;
		bg.scale(sx, sy);
		bg.drawImage(source, 0, 0, null);
		bg.dispose();
		return new LoadedTexture(dst, mcmeta);
	}

	public LoadedTexture tint(@Nullable KubeColor tint) {
		if (tint == null) {
			return this;
		}

		int argb = tint.kjs$getARGB();
		float l = ((argb >> 24) & 0xFF) / 255F;

		if (l <= 0F) {
			return this;
		} else if (l > 1F) {
			l = 1F;
		}

		float tr = ((argb >> 16) & 0xFF) / 255F;
		float tg = ((argb >> 8) & 0xFF) / 255F;
		float tb = (argb & 0xFF) / 255F;

		int[] result = new int[pixels.length];

		for (int i = 0; i < pixels.length; i++) {
			float pr = ((pixels[i] >> 16) & 0xFF) / 255F;
			float pg = ((pixels[i] >> 8) & 0xFF) / 255F;
			float pb = (pixels[i] & 0xFF) / 255F;

			result[i] = (pixels[i] & 0xFF000000)
				| ((int) (Mth.lerp(l, pr, pr * tr) * 255F) << 16)
				| ((int) (Mth.lerp(l, pg, pg * tg) * 255F) << 8)
				| (int) (Mth.lerp(l, pb, pb * tb) * 255F);
		}

		return new LoadedTexture(width, height, result, mcmeta);
	}
}
