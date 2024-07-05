package dev.latvian.mods.kubejs.script.data;

import dev.latvian.mods.kubejs.generator.KubeDataGenerator;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.server.packs.PackType;

public class VirtualDataPack extends VirtualResourcePack implements KubeDataGenerator {
	public VirtualDataPack(GeneratedDataStage stage) {
		super(ScriptType.SERVER, PackType.SERVER_DATA, stage);
	}
}
