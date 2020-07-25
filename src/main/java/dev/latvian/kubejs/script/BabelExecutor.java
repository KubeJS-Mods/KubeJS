package dev.latvian.kubejs.script;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.apache.commons.io.IOUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * @author LatvianModder
 */
public class BabelExecutor
{
	private static final ScriptEngine scriptEngine = new NashornScriptEngineFactory().getScriptEngine();
	private static final SimpleBindings bindings = new SimpleBindings();

	static
	{
		try (InputStreamReader babelScript = new InputStreamReader(BabelExecutor.class.getClassLoader().getResourceAsStream("babel.min.js")))
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
		bindings.put("input", IOUtils.toString(reader));
		return (String) scriptEngine.eval("Babel.transform(input, { presets: ['es2015'], sourceMaps: 'inline' }).code", bindings);
	}
}