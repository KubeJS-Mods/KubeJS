package dev.latvian.kubejs.script;

import dev.latvian.kubejs.KubeJS;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nullable;
import javax.script.Bindings;
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

	public boolean load(Bindings bindings)
	{
		error = null;

		try (InputStream stream = source.createStream(info))
		{
			String processedScript = BabelExecutor.process(new String(IOUtils.toByteArray(new BufferedInputStream(stream)), StandardCharsets.UTF_8));

			if (KubeJS.PRINT_PROCESSED_SCRIPTS)
			{
				KubeJS.LOGGER.info("Processed script: " + info.location + ":\n" + processedScript);
			}

			pack.engine.eval(processedScript, bindings);
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