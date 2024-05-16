package dev.latvian.mods.kubejs.util;

import com.google.gson.JsonElement;
import dev.latvian.mods.rhino.util.RemapForJS;

/**
 * @author LatvianModder
 */
public interface JsonSerializable {
	@RemapForJS("toJson")
	JsonElement toJsonJS();
}