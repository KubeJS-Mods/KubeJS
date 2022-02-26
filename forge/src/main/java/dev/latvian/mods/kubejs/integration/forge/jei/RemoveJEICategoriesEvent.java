package dev.latvian.mods.kubejs.integration.forge.jei;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.event.EventJS;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class RemoveJEICategoriesEvent extends EventJS {
	private final IJeiRuntime runtime;
	private final HashSet<ResourceLocation> categoriesRemoved;
	private final Collection<IRecipeCategory<?>> allCategories;

	public RemoveJEICategoriesEvent(IJeiRuntime r) {
		runtime = r;
		categoriesRemoved = new HashSet<>();
		allCategories = runtime.getRecipeManager().getRecipeCategories(Collections.emptyList(), false);
	}

	public Collection<IRecipeCategory<?>> getCategories() {
		return allCategories;
	}

	public void remove(String... categoriesToYeet) {
		for (var toYeet : categoriesToYeet) {
			categoriesRemoved.add(new ResourceLocation(toYeet));
		}
	}

	public void yeet(String... categoriesToRemove) {
		remove(categoriesToRemove);
	}

	public Collection<ResourceLocation> getCategoryIds() {
		Set<ResourceLocation> set = new HashSet<>();
		for (var allCategory : allCategories) {
            var uid = allCategory.getUid();
			set.add(uid);
		}
		return set;
	}

	public void removeIf(Predicate<IRecipeCategory<?>> filter) {
		allCategories.stream()
				.filter(filter)
				.map(IRecipeCategory::getUid)
				.map(ResourceLocation::toString)
				.forEach(this::yeet);
	}

	public void yeetIf(Predicate<IRecipeCategory<?>> filter) {
		removeIf(filter);
	}

	@Override
	protected void afterPosted(boolean result) {
		for (var category : categoriesRemoved) {
			try {
				runtime.getRecipeManager().hideRecipeCategory(category);
			} catch (Exception e) {
				KubeJS.LOGGER.warn("Failed to yeet recipe category {}!", category);
			}
		}
	}
}