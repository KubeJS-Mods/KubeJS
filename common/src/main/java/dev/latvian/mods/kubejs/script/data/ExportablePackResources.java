package dev.latvian.mods.kubejs.script.data;

import net.minecraft.server.packs.PackResources;

import java.io.IOException;
import java.nio.file.FileSystem;

public interface ExportablePackResources extends PackResources {
	void export(FileSystem fs) throws IOException;
}