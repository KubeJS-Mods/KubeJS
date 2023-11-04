package dev.latvian.mods.kubejs.client;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

public class StencilTexture {
	public int width;
	public int height;
	public int[] pixels;
	public byte[] mcmeta;

	public StencilTexture(BufferedImage img, byte[] mcmeta) {
		this.width = img.getWidth();
		this.height = img.getHeight();
		this.pixels = new int[width * height];
		img.getRGB(0, 0, width, height, pixels, 0, width);
		this.mcmeta = mcmeta;
	}

	public byte[] create(JsonObject colors) {
		var colorMap = new Int2IntArrayMap(colors.size());

		for (var entry : colors.entrySet()) {
			var k = entry.getKey();
			var v = entry.getValue().getAsString();
			int col = Integer.parseUnsignedInt(v.startsWith("#") ? v.substring(1) : v, 16);

			if ((col & 0xFF000000) == 0) {
				col |= 0xFF000000;
			}

			colorMap.put((Integer.parseUnsignedInt(k.startsWith("#") ? k.substring(1) : k, 16)) & 0xFFFFFF, col);
		}

		int[] result = new int[pixels.length];

		for (int i = 0; i < pixels.length; i++) {
			result[i] = ((pixels[i] & 0xFF000000) == 0) ? 0 : colorMap.getOrDefault(pixels[i] & 0xFFFFFF, pixels[i]);
		}

		var img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		img.setRGB(0, 0, width, height, result, 0, width);
		var out = new ByteArrayOutputStream();

		try {
			ImageIO.write(img, "png", out);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		return out.toByteArray();
	}
}
