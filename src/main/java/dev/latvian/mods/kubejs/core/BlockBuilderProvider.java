package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS("kjs$")
public interface BlockBuilderProvider {
	@Nullable
	default BlockBuilder kjs$getBlockBuilder() {
		throw new NoMixinException();
	}
}
