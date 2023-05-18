package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.platform.IngredientPlatformHelper;
import dev.latvian.mods.kubejs.recipe.component.ComponentValueMap;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaType;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.kubejs.util.WrappedJS;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RecipeFunction extends BaseFunction implements WrappedJS {
	private static final Pattern SKIP_ERROR = Pattern.compile("at dev.latvian.mods.kubejs.recipe.RecipeFunction.call");

	public final RecipesEventJS event;
	public final ResourceLocation id;
	public final String idString;
	public final RecipeSchemaType schemaType;

	public RecipeFunction(RecipesEventJS event, RecipeSchemaType schemaType) {
		this.event = event;
		this.id = schemaType.id;
		this.idString = id.toString();
		this.schemaType = schemaType;
	}

	@Override
	public RecipeJS call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args0) {
		return createRecipe(args0);
	}

	public RecipeJS createRecipe(Object[] args0) {
		try {
			if (schemaType.getSerializer() == null) {
				throw new RecipeExceptionJS("Unknown recipe type!");
			}

			var args1 = ListJS.of(args0);

			if (args1 == null || args1.isEmpty()) {
				throw new RecipeExceptionJS("Recipe requires at least one argument!");
			} else if (schemaType.schema.keys.length == 0 && args1.size() != 1) {
				throw new RecipeExceptionJS("Custom recipe has to use a single json object argument!");
			} else if (args1.size() == 1) {
				var map = MapJS.json(args1.get(0));

				if (map != null) {
					var recipe = schemaType.schema.deserialize(this, null, MapJS.json(normalize(map)));
					recipe.afterLoaded(true);
					return event.addRecipe(recipe, true);
				}
			}

			var constructor = schemaType.schema.constructors().get(args1.size());

			if (constructor == null) {
				throw new RecipeExceptionJS("Constructor for " + id + " with " + args1.size() + " arguments not found!");
			}

			var argMap = new ComponentValueMap(args1.size());
			int index = 0;

			for (var key : constructor.keys()) {
				argMap.put(key, args1.get(index++));
			}

			var recipe = constructor.factory().create(this, schemaType, argMap);
			recipe.afterLoaded(true);
			return event.addRecipe(recipe, false);
		} catch (RecipeExceptionJS ex) {
			ex.error();
			ConsoleJS.SERVER.error("Failed to create recipe for type '" + id + "'", ex, SKIP_ERROR);
		} catch (Throwable ex) {
			ConsoleJS.SERVER.handleError(ex, SKIP_ERROR, "Failed to create recipe for type '" + id + "' with args " + Arrays.toString(args0));
		}

		return new JsonRecipeJS();
	}

	private Object normalize(Object o) {
		if (o instanceof ItemStack stack) {
			return RecipeSchema.OUTPUT_ITEM.write(OutputItem.of(stack));
		} else if (o instanceof Ingredient ingr) {
			return ingr.toJson();
		} else if (o instanceof String s) {
			if (s.length() >= 4 && s.startsWith("#") && s.indexOf(':') != -1) {
				return IngredientPlatformHelper.get().tag(s.substring(1)).toJson();
			}
			return o;
		} else if (o instanceof Map<?, ?> m) {
			var map = new HashMap<>();

			for (var entry : m.entrySet()) {
				map.put(entry.getKey(), normalize(entry.getValue()));
			}

			return map;
		} else if (o instanceof Iterable<?> itr) {
			var list = new ArrayList<>();

			for (var o1 : itr) {
				list.add(normalize(o1));
			}

			return list;
		}

		return o;
	}

	@Override
	public String toString() {
		return idString;
	}

	public String getMod() {
		return id.getNamespace();
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return id.toString().equals(obj.toString());
	}
}