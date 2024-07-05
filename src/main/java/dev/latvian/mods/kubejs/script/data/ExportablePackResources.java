package dev.latvian.mods.kubejs.script.data;

import net.minecraft.server.packs.PackResources;

import java.io.IOException;
import java.nio.file.Path;

public interface ExportablePackResources extends PackResources {
	default String exportPath() {
		return packId();
	}

	void export(Path root) throws IOException;
}
