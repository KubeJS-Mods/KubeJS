package dev.latvian.mods.kubejs.text;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.network.chat.TextComponent;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public class TextString extends Text {
	private final String string;

	public TextString(@Nullable Object text) {
		string = String.valueOf(text);
	}

	public String getRawString() {
		return string;
	}

	@Override
	public TextComponent rawComponent() {
		return new TextComponent(string);
	}

	@Override
	public Text rawCopy() {
		return new TextString(string);
	}

	@Override
	public JsonElement toJson() {
		var o = getStyleAndSiblingJson();

		if (o.size() == 0) {
			return new JsonPrimitive(string);
		}

		o.addProperty("text", string);
		return o;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (!(obj instanceof TextString s) || !string.equals(s.string)) {
			return false;
		}

		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return string.hashCode() * 31 + super.hashCode();
	}
}