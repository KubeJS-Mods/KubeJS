package dev.latvian.mods.kubejs.client;

import net.minecraft.client.Minecraft;

import java.util.List;

/**
 * @author LatvianModder
 */
public class DebugInfoEventJS extends ClientEventJS {
	private final List<String> lines;

	public DebugInfoEventJS(List<String> l) {
		lines = l;
	}

	public boolean getShowDebug() {
		return Minecraft.getInstance().options.renderDebug;
	}

	public List<String> getLines() {
		return lines;
	}
}