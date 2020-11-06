package dev.latvian.kubejs.script;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.server.packs.resources.Resource;

/**
 * @author LatvianModder
 */
@FunctionalInterface
public interface ScriptSource
{
	InputStream createStream(ScriptFileInfo info) throws IOException;

	interface FromPath extends ScriptSource
	{
		Path getPath(ScriptFileInfo info);

		@Override
		default InputStream createStream(ScriptFileInfo info) throws IOException
		{
			return Files.newInputStream(getPath(info));
		}
	}

	interface FromResource extends ScriptSource
	{
		Resource getResource(ScriptFileInfo info) throws IOException;

		@Override
		default InputStream createStream(ScriptFileInfo info) throws IOException
		{
			return getResource(info).getInputStream();
		}
	}
}