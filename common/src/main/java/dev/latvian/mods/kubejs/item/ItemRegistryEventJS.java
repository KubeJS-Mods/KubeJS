package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.KubeJSObjects;
import dev.latvian.mods.kubejs.event.StartupEventJS;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class ItemRegistryEventJS extends StartupEventJS {
	public void create(String name, Consumer<ItemBuilder> callback) {
		var builder = new ItemBuilder(name);
		callback.accept(builder);
		KubeJSObjects.ITEMS.put(builder.id, builder);
		KubeJSObjects.ALL.add(builder);
	}

	public Supplier<FoodBuilder> createFood(Supplier<FoodBuilder> builder) {
		return builder;
	}
}