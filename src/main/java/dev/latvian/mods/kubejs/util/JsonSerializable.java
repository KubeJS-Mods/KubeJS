package dev.latvian.mods.kubejs.util;

import com.google.gson.JsonElement;
import dev.latvian.mods.rhino.Context;

public interface JsonSerializable {
	JsonElement toJson(Context cx);
}