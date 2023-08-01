package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Consumer;

@Info("""
	Invoked after all items are registered to modify them.
	""")
public class ItemModificationEventJS extends EventJS {

	@Info("""
		Modifies items matching the given ingredient.
					
		**NOTE**: tag ingredients are not supported at this time.
		""")
	public void modify(Ingredient in, Consumer<Item> c) {
		for (var item : in.kjs$getItemTypes()) {
			c.accept(item);
		}
	}
}
