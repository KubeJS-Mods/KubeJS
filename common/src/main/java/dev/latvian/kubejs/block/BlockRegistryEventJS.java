package dev.latvian.kubejs.block;

import dev.latvian.kubejs.KubeJSObjects;
import dev.latvian.kubejs.event.StartupEventJS;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class BlockRegistryEventJS extends StartupEventJS {
	@Deprecated
	public BlockBuilder create(String name) {
		BlockBuilder builder = new BlockBuilder(name);
		KubeJSObjects.BLOCKS.put(builder.id, builder);
		KubeJSObjects.ALL.add(builder);
		return builder;
	}

	public void create(String name, Consumer<BlockBuilder> callback) {
		BlockBuilder builder = new BlockBuilder(name);
		callback.accept(builder);
		KubeJSObjects.BLOCKS.put(builder.id, builder);
		KubeJSObjects.ALL.add(builder);
	}

	public void addDetector(String id) {
		if (id.isEmpty() || !id.equals(id.toLowerCase()) || id.matches("\\W")) {
			throw new IllegalArgumentException("Detector ID can only contain a-z _ and 0-9!");
		}

		KubeJSObjects.DETECTORS.put(id, new DetectorInstance(id));
	}
}