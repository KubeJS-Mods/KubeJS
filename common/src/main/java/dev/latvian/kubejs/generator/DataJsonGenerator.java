package dev.latvian.kubejs.generator;

import com.google.gson.JsonElement;
import dev.latvian.kubejs.util.ConsoleJS;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class DataJsonGenerator extends JsonGenerator {
	public DataJsonGenerator(Map<ResourceLocation, JsonElement> m) {
		super(ConsoleJS.SERVER, m);
	}
}
