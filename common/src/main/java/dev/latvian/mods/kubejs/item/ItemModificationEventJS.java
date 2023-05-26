package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Consumer;

public class ItemModificationEventJS extends EventJS {
	public void modify(Ingredient in, Consumer<Item> c) {
		for (var item : in.kjs$getItemTypes()) {
			c.accept(item);
		}
	}
}
