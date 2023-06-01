package dev.latvian.mods.kubejs.script.data;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPaths;
import net.minecraft.Util;
import net.minecraft.server.packs.FolderPackResources;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class KubeJSFolderPackResources extends FolderPackResources {
	public static final byte[] PACK_META_BYTES = Util.make(new JsonObject(), json -> {
		var pack = new JsonObject();
		pack.addProperty("description", "KubeJS Pack");
		pack.addProperty("pack_format", 8);
		json.add("pack", pack);
	}).toString().getBytes(StandardCharsets.UTF_8);

	public static final KubeJSFolderPackResources PACK = new KubeJSFolderPackResources(KubeJSPaths.DIRECTORY.toFile());

	private KubeJSFolderPackResources(File file) {
		super(file);
	}

	@Override
	protected InputStream getResource(String s) throws IOException {
		return switch (s) {
			case PACK_META -> new ByteArrayInputStream(PACK_META_BYTES);
			case "pack.png" -> Files.newInputStream(KubeJS.thisMod.findResource("kubejs_logo.png").get());
			default -> super.getResource(s);
		};
	}

	@Override
	protected boolean hasResource(String s) {
		return s.equals(PACK_META) || s.equals("pack.png") || !s.endsWith(".zip") && super.hasResource(s);
	}
}
