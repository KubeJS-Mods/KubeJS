package dev.latvian.kubejs.block;

import dev.latvian.kubejs.KubeJSObjects;
import dev.latvian.kubejs.event.EventJS;

/**
 * @author LatvianModder
 */
public class BlockRegistryEventJS extends EventJS {
	public BlockBuilder create(String name) {
		BlockBuilder builder = new BlockBuilder(name);
		KubeJSObjects.BLOCKS.put(builder.id, builder);
		KubeJSObjects.ALL.add(builder);
		return builder;
	}
}