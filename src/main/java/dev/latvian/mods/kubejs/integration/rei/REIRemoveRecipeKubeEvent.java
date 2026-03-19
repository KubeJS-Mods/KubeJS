package dev.latvian.mods.kubejs.integration.rei;
/*

import dev.latvian.mods.kubejs.recipe.viewer.RemoveRecipesKubeEvent;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.rhino.Context;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class REIRemoveRecipeKubeEvent implements RemoveRecipesKubeEvent {
	private final Map<CategoryIdentifier<?>, Collection<Identifier>> recipesRemoved;
	private final CategoryRegistry categories;

	public REIRemoveRecipeKubeEvent(Map<CategoryIdentifier<?>, Collection<Identifier>> recipesRemoved) {
		this.recipesRemoved = recipesRemoved;
		this.categories = CategoryRegistry.getInstance();
	}

	@Override
	public void remove(Context cx, Identifier[] recipesToRemove) {
		var asList = List.of(recipesToRemove);

		for (var catId : categories) {
			recipesRemoved.computeIfAbsent(catId.getCategoryIdentifier(), _0 -> new HashSet<>()).addAll(asList);
		}
	}

	@Override
	public void removeFromCategory(Context cx, @Nullable Identifier category, Identifier[] recipesToRemove) {
		if (category == null) {
			remove(cx, recipesToRemove);
			return;
		}

		var catId = CategoryIdentifier.of(String.valueOf(category));

		if (categories.tryGet(catId).isEmpty()) {
			((KubeJSContext) cx).getConsole().error("Failed to remove recipes for type '" + category + "': Category doesn't exist! Use 'event.categories' to get a list of all categories.");
			return;
		}

		recipesRemoved.computeIfAbsent(catId, _0 -> new HashSet<>()).addAll(List.of(recipesToRemove));
	}
}
*/
