package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.client.Minecraft;

import java.util.List;

@Info("""
		Invoked when the debug info is rendered.
		""")
public class DebugInfoEventJS extends ClientEventJS {
	private final List<String> lines;

	public DebugInfoEventJS(List<String> l) {
		lines = l;
	}

	@Info("Whether the debug info should be rendered.")
	public boolean getShowDebug() {
		return Minecraft.getInstance().options.renderDebug;
	}

	@Info("The lines of debug info. Mutating this list will change the debug info.")
	public List<String> getLines() {
		return lines;
	}
}