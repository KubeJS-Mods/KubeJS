package dev.latvian.kubejs.script;

import org.apache.commons.io.IOUtils;

import javax.annotation.Nullable;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author LatvianModder
 */
public class ScriptFile implements Comparable<ScriptFile>
{
	public final ScriptPack pack;
	public final ScriptFileInfo info;
	public final ScriptSource source;

	private Throwable error;

	public ScriptFile(ScriptPack p, ScriptFileInfo i, ScriptSource s)
	{
		pack = p;
		info = i;
		source = s;
	}

	@Nullable
	public Throwable getError()
	{
		return error;
	}

	public boolean load()
	{
		error = null;

		try (InputStream stream = source.createStream(info))
		{
			String script = new String(IOUtils.toByteArray(new BufferedInputStream(stream)), StandardCharsets.UTF_8);
			pack.context.evaluateString(pack.scope, script, info.location.toString(), 1, null);
			return true;
		}
		catch (Throwable ex)
		{
			error = ex;
			return false;
		}
	}

	@Override
	public int compareTo(ScriptFile o)
	{
		return Integer.compare(o.info.getPriority(), info.getPriority());
	}
}