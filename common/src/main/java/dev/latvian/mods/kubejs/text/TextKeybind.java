package dev.latvian.mods.kubejs.text;

import com.google.gson.JsonObject;
import net.minecraft.network.chat.KeybindComponent;

/**
 * @author LatvianModder
 */
public class TextKeybind extends Text {
	private final String keybind;

	public TextKeybind(String k) {
		keybind = k;
	}

	public String getKeybind() {
		return keybind;
	}

	@Override
	public KeybindComponent rawComponent() {
		return new KeybindComponent(keybind);
	}

	@Override
	public Text rawCopy() {
		return new TextKeybind(keybind);
	}

	@Override
	public JsonObject toJson() {
		JsonObject o = getStyleAndSiblingJson();
		o.addProperty("keybind", keybind);
		return o;
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof TextKeybind && keybind.equals(((TextKeybind) obj).keybind);
	}

	@Override
	public int hashCode() {
		return keybind.hashCode() * 31 + super.hashCode();
	}
}