package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.event.EventHandler;
import net.minecraft.client.Minecraft;

import java.util.List;

/**
 * @author LatvianModder
 */
public class DebugInfoEventJS extends ClientEventJS {
	public static final EventHandler LEFT_EVENT = EventHandler.client(DebugInfoEventJS.class).legacy("client.debug_info.left");
	public static final EventHandler RIGHT_EVENT = EventHandler.client(DebugInfoEventJS.class).legacy("client.debug_info.right");

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