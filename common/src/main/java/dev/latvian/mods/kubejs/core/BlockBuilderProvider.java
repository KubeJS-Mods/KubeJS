package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import org.jetbrains.annotations.Nullable;

public interface BlockBuilderProvider {
	@Nullable
	BlockBuilder getBlockBuilderKJS();
}
