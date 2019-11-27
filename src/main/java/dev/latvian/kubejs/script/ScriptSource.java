package dev.latvian.kubejs.script;

import java.io.IOException;
import java.io.Reader;

/**
 * @author LatvianModder
 */
@FunctionalInterface
public interface ScriptSource
{
	Reader createReader(ScriptFileInfo info) throws IOException;
}