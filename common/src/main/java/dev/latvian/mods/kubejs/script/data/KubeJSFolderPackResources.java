package dev.latvian.mods.kubejs.script.data;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPaths;
import net.minecraft.Util;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class KubeJSFolderPackResources extends PathPackResources {
	public static final byte[] PACK_META_BYTES = Util.make(new JsonObject(), json -> {
		var pack = new JsonObject();
		pack.addProperty("description", "KubeJS Pack");
		pack.addProperty("pack_format", 8);
		json.add("pack", pack);
	}).toString().getBytes(StandardCharsets.UTF_8);

	public static final KubeJSFolderPackResources PACK = new KubeJSFolderPackResources(KubeJSPaths.DIRECTORY);

	private KubeJSFolderPackResources(Path path) {
		super("KubeJS Folder Resources", path, true);
	}

	@Nullable
	@Override
	public IoSupplier<InputStream> getRootResource(String... path) {
		var joined = String.join("/", path);
		return switch (joined) {
			case PACK_META -> () -> new ByteArrayInputStream(PACK_META_BYTES);
			case "pack.png" -> IoSupplier.create(KubeJS.thisMod.findResource("kubejs_logo.png").get());
			default -> super.getRootResource(path);
		};
	}

	// TODO: Filter .zip files out of resource listings
	/*@Override
	protected boolean hasResource(String s) {
		return s.equals(PACK_META) || s.equals("pack.png") || !s.endsWith(".zip") && super.hasResource(s);
	}*/
}
