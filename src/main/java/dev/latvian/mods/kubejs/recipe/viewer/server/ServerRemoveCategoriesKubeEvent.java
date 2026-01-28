package dev.latvian.mods.kubejs.recipe.viewer.server;

import dev.latvian.mods.kubejs.recipe.viewer.RemoveCategoriesKubeEvent;
import dev.latvian.mods.rhino.Context;
import net.minecraft.resources.Identifier;

import java.util.Arrays;
import java.util.Set;

public class ServerRemoveCategoriesKubeEvent implements RemoveCategoriesKubeEvent {
	private final Set<Identifier> categories;

	public ServerRemoveCategoriesKubeEvent(Set<Identifier> categories) {
		this.categories = categories;
	}

	@Override
	public void remove(Context cx, Identifier[] categories) {
		this.categories.addAll(Arrays.asList(categories));
	}
}
