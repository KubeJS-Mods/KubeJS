package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import org.jspecify.annotations.NullUnmarked;

@RemapPrefixForJS("kjs$")
public interface BlockBuilderProvider {
	@NullUnmarked
	default BlockBuilder kjs$getBlockBuilder() {
		throw new NoMixinException();
	}
}
