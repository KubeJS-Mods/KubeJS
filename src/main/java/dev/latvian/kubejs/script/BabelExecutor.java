package dev.latvian.kubejs.script;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.apache.commons.io.IOUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * @author LatvianModder
 */
public class BabelExecutor
{
	private static boolean inited = false;
	private static ScriptEngine scriptEngine;
	private static SimpleBindings bindings;

	private static void init()
	{
		if (inited)
		{
			return;
		}

		inited = true;
		scriptEngine = new NashornScriptEngineFactory().getScriptEngine();
		bindings = new SimpleBindings();

		try (InputStreamReader babelScript = new InputStreamReader(BabelExecutor.class.getResourceAsStream("/data/kubejs/babel.min.js"), StandardCharsets.UTF_8))
		{
			try
			{
				scriptEngine.eval(babelScript, bindings);
			}
			catch (ScriptException e)
			{
				throw new RuntimeException(e);
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static String process(Reader reader) throws IOException, ScriptException
	{
		init();
		bindings.put("input", IOUtils.toString(reader));
		return scriptEngine.eval("Babel.transform(input, { presets: ['es2015'], sourceMaps: 'inline' }).code", bindings).toString();
	}
}