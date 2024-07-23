package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.event.KubeStartupEvent;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.KubeResourceLocation;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.crafting.Ingredient;

public class ItemModelPropertiesKubeEvent implements KubeStartupEvent {

	@Info("""
		Register a model property for an item. Model properties are used to change the appearance of an item in the world.
				
		More about model properties: https://minecraft.wiki/w/Tutorials/Models#Item_predicates
		""")
	public void register(Ingredient ingredient, KubeResourceLocation overwriteId, ClampedItemPropertyFunction callback) {
		var id = overwriteId.wrapped();

		if (ingredient.kjs$isWildcard()) {
			ItemProperties.registerGeneric(id, callback);

		} else {
			for (var stack : ingredient.kjs$getStacks()) {
				ItemProperties.register(stack.getItem(), id, callback);
			}
		}
	}

	@Info("Register a model property for all items.")
	public void registerAll(KubeResourceLocation overwriteId, ClampedItemPropertyFunction callback) {
		ItemProperties.registerGeneric(overwriteId.wrapped(), callback);
	}
}
