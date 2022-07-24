package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import net.minecraft.world.item.Item;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class ItemModificationEventJS extends EventJS {
	public void modify(IngredientJS in, Consumer<Item> c) {
		for (var item : in.getVanillaItems()) {
			c.accept(item);
		}
	}
}
