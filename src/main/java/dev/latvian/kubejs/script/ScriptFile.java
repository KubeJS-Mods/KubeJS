package dev.latvian.kubejs.script;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;

import javax.annotation.Nullable;
import javax.script.Bindings;
import java.io.Reader;

public class ScriptFile implements Comparable<ScriptFile>
{
	@FunctionalInterface
	public interface ScriptSource
	{
		Reader createReader() throws Throwable;
	}

	public final ScriptPack pack;
	public final String path;
	private final ScriptSource source;
	private final int order;
	private Throwable error;

	public ScriptFile(ScriptPack pc, String p, int o, ScriptSource s)
	{
		pack = pc;
		path = p;
		source = s;
		order = o;
		error = null;
	}

	@Nullable
	public Throwable getError()
	{
		return error;
	}

	public boolean load(Bindings bindings)
	{
		error = null;

		try (Reader reader = source.createReader())
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
		int i = pack.compareTo(o.pack);

		if (i != 0)
		{
			return i;
		}

		if (order != o.order)
		{
			return Integer.compare(order, o.order);
		}

		return path.compareToIgnoreCase(o.path);
	}

	@Nullable
	public ITextComponent getErrorTextComponent()
	{
		if (error == null)
		{
			return null;
		}

		ITextComponent errorc = new TextComponentString("Error in ");
		errorc.getStyle().setColor(TextFormatting.RED);
		ITextComponent pathc = new TextComponentString(path);
		pathc.getStyle().setColor(TextFormatting.GOLD);
		ITextComponent result = new TextComponentString("").appendSibling(errorc).appendSibling(pathc);
		String errorString = error.toString().replace("javax.script.ScriptException: ", "");
		result.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(errorString)));
		return result;
	}
}