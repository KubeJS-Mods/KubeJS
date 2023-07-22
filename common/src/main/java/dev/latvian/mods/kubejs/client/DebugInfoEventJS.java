package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.typings.JsInfo;
import net.minecraft.client.Minecraft;

import java.util.List;

@JsInfo("""
		Invoked when the debug info is rendered.
		""")
public class DebugInfoEventJS extends ClientEventJS {
	private final List<String> lines;

	public DebugInfoEventJS(List<String> l) {
		lines = l;
	}

	@JsInfo("Whether the debug info should be rendered.")
	public boolean getShowDebug() {
		return Minecraft.getInstance().options.renderDebug;
	}

	@JsInfo("The lines of debug info. Mutating this list will change the debug info.")
	public List<String> getLines() {
		return lines;
	}
}