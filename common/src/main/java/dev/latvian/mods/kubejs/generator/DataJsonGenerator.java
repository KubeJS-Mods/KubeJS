package dev.latvian.mods.kubejs.generator;

import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class DataJsonGenerator extends JsonGenerator {
	public DataJsonGenerator(Map<ResourceLocation, byte[]> m) {
		super(ConsoleJS.SERVER, m);
	}
}
