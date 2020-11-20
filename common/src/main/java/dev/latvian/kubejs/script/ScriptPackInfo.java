package dev.latvian.kubejs.script;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ScriptPackInfo
{
	public final String namespace;
	public final Component displayName;
	public final List<ScriptFileInfo> scripts;
	public final String pathStart;

	public ScriptPackInfo(String n, String p)
	{
		namespace = n;
		scripts = new ArrayList<>();
		pathStart = p;
		displayName = new TextComponent(namespace); // Load custom properties
	}
}