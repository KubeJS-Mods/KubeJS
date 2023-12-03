package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.kubejs.util.LogType;
import net.minecraft.network.chat.Component;

import java.nio.file.Path;

public class ConsoleLine {
	public static final ConsoleLine[] EMPTY_ARRAY = new ConsoleLine[0];

	public final long timestamp;
	public String message;
	public Component messageComponent;
	public LogType type = LogType.INFO;
	public String group = "";
	public String source = null;
	public int line = 0;
	public Path externalFile = null;
	public Throwable error = null;
	private String cachedText;

	public ConsoleLine(String message) {
		this.timestamp = System.currentTimeMillis();
		this.message = message;
	}

	public ConsoleLine(Component messageComponent) {
		this.timestamp = System.currentTimeMillis();
		this.messageComponent = messageComponent;
	}

	public String getText() {
		if (cachedText == null) {
			var builder = new StringBuilder();

			if (line > 0) {
				if (source != null && !source.isEmpty()) {
					builder.append(source);
					builder.append('#');
				} else {
					builder.append("<unknown source>#");
				}

				builder.append(line);
				builder.append(": ");
			}

			if (!group.isEmpty()) {
				builder.append(group);
			}

			builder.append(message == null ? messageComponent.getString() : message);
			cachedText = builder.toString();
		}

		return cachedText;
	}

	@Override
	public String toString() {
		return getText();
	}
}
