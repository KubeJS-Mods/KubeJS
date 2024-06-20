package dev.latvian.mods.kubejs.recipe.viewer.server;

import dev.latvian.mods.kubejs.recipe.viewer.RemoveCategoriesKubeEvent;
import dev.latvian.mods.rhino.Context;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

public class ServerRemoveCategoriesKubeEvent implements RemoveCategoriesKubeEvent {
	private final Set<ResourceLocation> categories;

	public ServerRemoveCategoriesKubeEvent(Set<ResourceLocation> categories) {
		this.categories = categories;
	}

	@Override
	public void remove(Context cx, ResourceLocation[] categories) {
		this.categories.addAll(Arrays.asList(categories));
	}

	@Override
	public Collection<ResourceLocation> getCategories() {
		throw new IllegalStateException("Not available on server side!");
	}
}
