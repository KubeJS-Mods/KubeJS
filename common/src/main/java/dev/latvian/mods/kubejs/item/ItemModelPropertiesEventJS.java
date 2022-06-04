package dev.latvian.mods.kubejs.item;

import dev.architectury.registry.item.ItemPropertiesRegistry;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.core.AsKJS;
import dev.latvian.mods.kubejs.core.EntityKJS;
import dev.latvian.mods.kubejs.entity.EntityJS;
import dev.latvian.mods.kubejs.event.StartupEventJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.item.ingredient.MatchAllIngredientJS;
import dev.latvian.mods.kubejs.level.LevelJS;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class ItemModelPropertiesEventJS extends StartupEventJS {
	public void register(IngredientJS ingredient, String overwriteId, ItemPropertiesCallback callback) {
		if (ingredient instanceof MatchAllIngredientJS) {
			registerAll(overwriteId, callback);
		} else {
			ingredient.getStacks().forEach(stack -> {
				ItemPropertiesRegistry.register(stack.getItem(), new ResourceLocation(KubeJS.appendModId(overwriteId)), wrap(callback));
			});
		}
	}

	public void registerAll(String overwriteId, ItemPropertiesCallback callback) {
		ItemPropertiesRegistry.registerGeneric(new ResourceLocation(KubeJS.appendModId(overwriteId)), wrap(callback));
	}

	private ClampedItemPropertyFunction wrap(ItemPropertiesCallback callback) {
		return (itemStack, level, entity, id) -> callback.accept(ItemStackJS.of(itemStack), AsKJS.wrapSafe(level), AsKJS.wrapSafe(entity), id);
	}

	@FunctionalInterface
	public interface ItemPropertiesCallback {
		float accept(ItemStackJS stack, @Nullable LevelJS level, @Nullable EntityJS entity, int id);
	}
}
