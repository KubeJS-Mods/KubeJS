package dev.latvian.kubejs.script;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ScriptPackInfo
{
	public final String namespace;
	public final ITextComponent displayName;
	public final List<ScriptFileInfo> scripts;
	public final String pathStart;

	public ScriptPackInfo(String n, String p)
	{
		namespace = n;
		scripts = new ArrayList<>();
		pathStart = p;
		displayName = new StringTextComponent(namespace); // Load custom properties
	}
}