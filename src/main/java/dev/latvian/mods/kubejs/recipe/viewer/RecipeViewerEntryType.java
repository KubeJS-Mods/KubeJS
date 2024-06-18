package dev.latvian.mods.kubejs.recipe.viewer;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.event.Extra;
import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.Lazy;
import dev.latvian.mods.rhino.type.TypeInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Note: predicateType has to be able to be cast to {@link java.util.function.Predicate} of entryType
 */
public record RecipeViewerEntryType(String id, TypeInfo entryType, TypeInfo predicateType) {
	public static final RecipeViewerEntryType ITEM = new RecipeViewerEntryType("item", ItemStackJS.TYPE_INFO, IngredientJS.TYPE_INFO);
	public static final RecipeViewerEntryType FLUID = new RecipeViewerEntryType("fluid", FluidWrapper.TYPE_INFO, FluidWrapper.INGREDIENT_TYPE_INFO);

	public static Lazy<Map<String, RecipeViewerEntryType>> CUSTOM_TYPES = Lazy.of(() -> {
		var map = new HashMap<String, RecipeViewerEntryType>();
		KubeJSPlugins.forEachPlugin(t -> map.put(t.id, t), KubeJSPlugin::registerRecipeViewerEntryTypes);
		return Map.copyOf(map);
	});

	public static final Lazy<List<RecipeViewerEntryType>> ALL_TYPES = Lazy.of(() -> {
		var list = new ArrayList<RecipeViewerEntryType>();
		list.add(ITEM);
		list.add(FLUID);
		list.addAll(CUSTOM_TYPES.get().values());
		return List.copyOf(list);
	});

	public static final Extra<RecipeViewerEntryType> EXTRA = Extra.create(RecipeViewerEntryType.class).transformer(id -> switch (id == null ? "" : id.toString()) {
		case null -> null;
		case "" -> null;
		case "item" -> ITEM;
		case "fluid" -> FLUID;
		default -> CUSTOM_TYPES.get().get(String.valueOf(id));
	}).identity();
}
