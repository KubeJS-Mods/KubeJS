package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.kubejs.util.ID;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.Locale;
import java.util.regex.Pattern;

public class ScriptFileInfo {
	private static final Pattern FILE_FIXER = Pattern.compile("[^\\w./]");

	public final ScriptPackInfo pack;
	public final Path path;
	public final String file;
	public final ResourceLocation id;
	public final String location;

	public ScriptFileInfo(ScriptPackInfo p, Path ph, String f) {
		pack = p;
		path = ph;
		file = f;
		id = ResourceLocation.fromNamespaceAndPath(pack.namespace, FILE_FIXER.matcher(pack.pathStart + file).replaceAll("_").toLowerCase(Locale.ROOT));
		location = ID.string(pack.namespace + ":" + pack.pathStart + file);
	}
}