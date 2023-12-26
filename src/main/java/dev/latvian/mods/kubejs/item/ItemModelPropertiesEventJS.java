package dev.latvian.mods.kubejs.item;

import dev.architectury.registry.item.ItemPropertiesRegistry;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.event.StartupEventJS;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

public class ItemModelPropertiesEventJS extends StartupEventJS {

	@Info("""
		Register a model property for an item. Model properties are used to change the appearance of an item in the world.
					
		More about model properties: https://minecraft.fandom.com/wiki/Model#Item_predicates
		""")
	public void register(Ingredient ingredient, String overwriteId, ClampedItemPropertyFunction callback) {
		if (ingredient.kjs$isWildcard()) {
			registerAll(overwriteId, callback);
		} else {
			for (var stack : ingredient.kjs$getStacks()) {
				ItemPropertiesRegistry.register(stack.getItem(), new ResourceLocation(KubeJS.appendModId(overwriteId)), callback);
			}
		}
	}

	@Info("Register a model property for all items.")
	public void registerAll(String overwriteId, ClampedItemPropertyFunction callback) {
		ItemPropertiesRegistry.registerGeneric(new ResourceLocation(KubeJS.appendModId(overwriteId)), callback);
	}
}
