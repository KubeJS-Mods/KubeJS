package dev.latvian.mods.kubejs.recipe.viewer.server;

import dev.latvian.mods.kubejs.recipe.viewer.RemoveRecipesKubeEvent;
import dev.latvian.mods.rhino.Context;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class ServerRemoveRecipesKubeEvent implements RemoveRecipesKubeEvent {
	private final Set<Identifier> global;
	private final Map<Identifier, CategoryData> categoryData;

	public ServerRemoveRecipesKubeEvent(Set<Identifier> global, Map<Identifier, CategoryData> categoryData) {
		this.global = global;
		this.categoryData = categoryData;
	}

	@Override
	public void remove(Context cx, Identifier[] recipesToRemove) {
		this.global.addAll(Arrays.asList(recipesToRemove));
	}

	@Override
	public void removeFromCategory(Context cx, @Nullable Identifier category, Identifier[] recipesToRemove) {
		if (category == null) {
			remove(cx, recipesToRemove);
			return;
		}

		categoryData.computeIfAbsent(category, CategoryData::new).removedRecipes().addAll(Arrays.asList(recipesToRemove));
	}
}
