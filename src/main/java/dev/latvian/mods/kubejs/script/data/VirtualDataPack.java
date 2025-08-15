package dev.latvian.mods.kubejs.script.data;

import dev.latvian.mods.kubejs.generator.KubeDataGenerator;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import net.minecraft.server.packs.PackType;

import java.util.function.Supplier;

public class VirtualDataPack extends VirtualResourcePack implements KubeDataGenerator {
	public VirtualDataPack(GeneratedDataStage stage, Supplier<RegistryAccessContainer> registries) {
		super(ScriptType.SERVER, PackType.SERVER_DATA, stage, registries);
	}
}
