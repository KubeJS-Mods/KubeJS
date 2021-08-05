package dev.latvian.kubejs.integration.rei;

import dev.latvian.kubejs.event.EventJS;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.utils.CollectionUtils;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

public class RemoveREICategoryEventJS extends EventJS {
	private final Set<ResourceLocation> categoriesRemoved;

	public RemoveREICategoryEventJS(Set<ResourceLocation> categoriesRemoved) {
		this.categoriesRemoved = categoriesRemoved;
	}

	public Collection<String> getCategories() {
		return CollectionUtils.map(RecipeHelper.getInstance().getAllCategories(), category -> category.getIdentifier().toString());
	}

	public void remove(ResourceLocation[] categoriesToYeet) {
		categoriesRemoved.addAll(Arrays.asList(categoriesToYeet));
	}

	public void yeet(ResourceLocation[] categoriesToYeet) {
		remove(categoriesToYeet);
	}
}
