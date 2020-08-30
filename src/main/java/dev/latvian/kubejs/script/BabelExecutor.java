package dev.latvian.kubejs.script;

import dev.latvian.kubejs.KubeJS;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.apache.commons.io.IOUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author LatvianModder
 */
public class BabelExecutor
{
	private static boolean inited = false;
	private static ScriptEngine scriptEngine;
	private static SimpleBindings bindings;

	public static void init()
	{
		if (inited)
		{
			return;
		}

		long start = System.currentTimeMillis();
		inited = true;
		scriptEngine = new NashornScriptEngineFactory().getScriptEngine();
		bindings = new SimpleBindings();

		try (InputStream stream = BabelExecutor.class.getResourceAsStream("/data/kubejs/babel.min.js"))
		{
			String script = new String(IOUtils.toByteArray(new BufferedInputStream(stream)), StandardCharsets.UTF_8);
			scriptEngine.eval(script, bindings);
		}
		catch (ScriptException | IOException e)
		{
			throw new RuntimeException(e);
		}

		KubeJS.LOGGER.info("Loaded babel.min.js in " + (System.currentTimeMillis() - start) / 1000D + " s");
	}

	public static String process(String string) throws ScriptException
	{
		init();
		bindings.put("input", string);
		return scriptEngine.eval("Babel.transform(input, { presets: ['es2015'], sourceMaps: true, retainLines: true, sourceType: 'script' }).code", bindings).toString();
	}
}