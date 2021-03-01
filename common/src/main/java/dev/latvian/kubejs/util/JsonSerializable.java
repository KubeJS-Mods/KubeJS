package dev.latvian.kubejs.util;

import com.google.gson.JsonElement;

/**
 * @author LatvianModder
 */
@FunctionalInterface
public interface JsonSerializable {
	JsonElement toJson();
}