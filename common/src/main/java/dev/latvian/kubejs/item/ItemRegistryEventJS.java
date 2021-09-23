package dev.latvian.kubejs.item;

import dev.latvian.kubejs.KubeJSObjects;
import dev.latvian.kubejs.event.EventJS;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class ItemRegistryEventJS extends EventJS {
	@Deprecated
	public ItemBuilder create(String name) {
		ItemBuilder builder = new ItemBuilder(name);
		KubeJSObjects.ITEMS.put(builder.id, builder);
		KubeJSObjects.ALL.add(builder);
		return builder;
	}

	public void create(String name, Consumer<ItemBuilder> callback) {
		ItemBuilder builder = new ItemBuilder(name);
		callback.accept(builder);
		KubeJSObjects.ITEMS.put(builder.id, builder);
		KubeJSObjects.ALL.add(builder);
	}

	public Supplier<FoodBuilder> createFood(Supplier<FoodBuilder> builder) {
		return builder;
	}
}