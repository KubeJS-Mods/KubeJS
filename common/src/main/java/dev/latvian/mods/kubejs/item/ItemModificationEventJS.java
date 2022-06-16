package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class ItemModificationEventJS extends EventJS {
	public void modify(IngredientJS in, Consumer<ItemModificationProperties> c) {
		for (var item : in.getVanillaItems()) {
			c.accept(new ItemModificationProperties(item));
		}
	}
}
