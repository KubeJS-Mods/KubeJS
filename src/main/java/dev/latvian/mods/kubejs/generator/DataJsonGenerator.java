package dev.latvian.mods.kubejs.generator;

import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.script.data.GeneratedData;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class DataJsonGenerator extends ResourceGenerator {
	public DataJsonGenerator(Map<ResourceLocation, GeneratedData> m) {
		super(ConsoleJS.SERVER, m);
	}
}
