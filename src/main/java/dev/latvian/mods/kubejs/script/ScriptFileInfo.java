package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.kubejs.util.ID;
import net.minecraft.resources.Identifier;

import java.nio.file.Path;
import java.util.Locale;
import java.util.regex.Pattern;

/// Metadata for a single script file discovered on the filesystem.
/// Stores file path, id and name as well as the associated [ScriptPackInfo].
///
/// Non-word characters in the path are normalized to `_` when building the identifier.
public class ScriptFileInfo {
	private static final Pattern FILE_FIXER = Pattern.compile("[^\\w./]");

	public final ScriptPackInfo pack;
	public final Path path;
	public final String file;
	public final Identifier id;
	public final String locationPath;
	public final String location;

	public ScriptFileInfo(ScriptPackInfo p, Path ph, String f) {
		this.pack = p;
		this.path = ph;
		this.file = f;
		this.id = Identifier.fromNamespaceAndPath(pack.namespace, FILE_FIXER.matcher(pack.pathStart + file).replaceAll("_").toLowerCase(Locale.ROOT));
		this.locationPath = pack.pathStart + file;
		this.location = ID.string(pack.namespace + ":" + locationPath);
	}
}