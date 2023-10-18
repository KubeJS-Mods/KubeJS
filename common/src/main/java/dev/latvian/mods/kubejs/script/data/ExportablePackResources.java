package dev.latvian.mods.kubejs.script.data;

import net.minecraft.server.packs.PackResources;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;

public interface ExportablePackResources extends PackResources {
	void export(Path root) throws IOException;
}