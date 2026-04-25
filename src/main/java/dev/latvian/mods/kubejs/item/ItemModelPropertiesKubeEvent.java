package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.client.model.KubeJSModelPropertyRegistry;
import dev.latvian.mods.kubejs.event.KubeStartupEvent;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.KubeIdentifier;
import net.minecraft.client.renderer.item.properties.conditional.ItemModelPropertyTest;

public class ItemModelPropertiesKubeEvent implements KubeStartupEvent {

	@Info("""
		Register a model property for an item. Model properties are used to change the appearance of an item in the world.
		
		More about model properties: https://minecraft.wiki/w/Tutorials/Models#Item_predicates
		""")
	public void register(KubeIdentifier overwriteId, ItemModelPropertyTest callback) {
		KubeJSModelPropertyRegistry.putConditional(overwriteId.wrapped(), callback);
	}


	@Info("Register a model property callback by id. Any item model that references this id will evaluate it.")
	public void registerAll(KubeIdentifier overwriteId, ItemModelPropertyTest callback) {
		KubeJSModelPropertyRegistry.putConditional(overwriteId.wrapped(), callback);
	}

}
