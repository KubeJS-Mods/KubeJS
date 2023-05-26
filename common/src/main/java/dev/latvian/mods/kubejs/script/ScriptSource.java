package dev.latvian.mods.kubejs.script;

import net.minecraft.server.packs.resources.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@FunctionalInterface
public interface ScriptSource {
	InputStream createStream(ScriptFileInfo info) throws IOException;

	interface FromPath extends ScriptSource {
		Path getPath(ScriptFileInfo info);

		@Override
		default InputStream createStream(ScriptFileInfo info) throws IOException {
			return Files.newInputStream(getPath(info));
		}
	}

	interface FromResource extends ScriptSource {
		Resource getResource(ScriptFileInfo info) throws IOException;

		@Override
		default InputStream createStream(ScriptFileInfo info) throws IOException {
			return getResource(info).open();
		}
	}
}