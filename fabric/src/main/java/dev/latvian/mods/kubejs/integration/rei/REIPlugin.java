package dev.latvian.mods.kubejs.integration.rei;

import dev.architectury.event.EventResult;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.CollectionUtils;
import me.shedaniel.rei.api.common.util.EntryIngredients;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * @author shedaniel
 */
public class REIPlugin implements REIClientPlugin {
	private final Set<CategoryIdentifier<?>> categoriesRemoved = new HashSet<>();

	@Override
	public void registerEntries(EntryRegistry registry) {
		Function<Object, Collection<EntryStack<?>>> itemSerializer = o -> EntryIngredients.ofItemStacks(CollectionUtils.map(IngredientJS.of(o).getStacks(), ItemStackJS::getItemStack));

		new HideREIEventJS<>(registry, VanillaEntryTypes.ITEM, itemSerializer).post(ScriptType.CLIENT, REIIntegration.REI_HIDE_ITEMS);
		new AddREIEventJS(registry, itemSerializer).post(ScriptType.CLIENT, REIIntegration.REI_ADD_ITEMS);
	}

	@Override
	public void registerDisplays(DisplayRegistry registry) {
		new InformationREIEventJS().post(ScriptType.CLIENT, REIIntegration.REI_INFORMATION);
	}

	@Override
	public void registerCategories(CategoryRegistry registry) {
		registry.registerVisibilityPredicate(category -> {
			return categoriesRemoved.contains(category.getCategoryIdentifier()) ? EventResult.interruptFalse() : EventResult.pass();
		});
	}

	// TODO: should be an easy fix i'm just lazy
	/*@Override
	public void postRegister() {
		categoriesRemoved.clear();
		new RemoveREICategoryEventJS(categoriesRemoved).post(ScriptType.CLIENT, REIIntegration.REI_REMOVE_CATEGORIES);
	}*/
}