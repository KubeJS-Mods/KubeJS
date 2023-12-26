package dev.latvian.mods.kubejs.script;

import net.minecraft.server.packs.resources.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@FunctionalInterface
public interface ScriptSource {
	List<String> readSource(ScriptFileInfo info) throws IOException;

	interface FromPath extends ScriptSource {
		Path getPath(ScriptFileInfo info);

		@Override
		default List<String> readSource(ScriptFileInfo info) throws IOException {
			return Files.readAllLines(getPath(info));
		}
	}

	interface FromResource extends ScriptSource {
		Resource getResource(ScriptFileInfo info) throws IOException;

		@Override
		default List<String> readSource(ScriptFileInfo info) throws IOException {
			var list = new ArrayList<String>();

			try (var reader = getResource(info).openAsReader()) {
				String line;

				while ((line = reader.readLine()) != null) {
					list.add(line);
				}

				return list;
			}
		}
	}
}