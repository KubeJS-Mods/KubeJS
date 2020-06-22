package dev.latvian.kubejs.script;

import javax.annotation.Nullable;
import javax.script.Bindings;
import java.io.Reader;

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

	public boolean load(Bindings bindings)
	{
		error = null;

		try (Reader reader = source.createReader(info))
		{
			pack.engine.eval(reader, bindings);
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