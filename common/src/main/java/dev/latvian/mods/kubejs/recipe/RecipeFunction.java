package dev.latvian.mods.kubejs.recipe;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.platform.IngredientPlatformHelper;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author LatvianModder
 */
public class RecipeFunction extends BaseFunction implements WrappedJS {
	private static final Pattern SKIP_ERROR = Pattern.compile("at dev.latvian.mods.kubejs.recipe.RecipeFunction.call");

	private final RecipesEventJS event;
	public final ResourceLocation typeID;
	public final RecipeTypeJS type;

	public RecipeFunction(RecipesEventJS e, ResourceLocation id, @Nullable RecipeTypeJS t) {
		event = e;
		typeID = id;
		type = t;
	}

	@Override
	public RecipeJS call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args0) {
		return createRecipe(args0);
	}

	public RecipeJS createRecipe(Object[] args0) {
		try {
			if (type == null) {
				throw new RecipeExceptionJS("Unknown recipe type!");
			}

			var args1 = ListJS.of(args0);

			if (args1 == null || args1.isEmpty()) {
				throw new RecipeExceptionJS("Recipe requires at least one argument!");
			} else if (type.isCustom() && args1.size() != 1) {
				throw new RecipeExceptionJS("Custom recipe has to use a single json object argument!");
			} else if (args1.size() == 1) {
				var map = MapJS.json(args1.get(0));

				if (map != null) {
					var recipe = type.factory.get();
					RecipeArguments args = new RecipeArguments(recipe, args1);
					recipe.type = type;
					recipe.json = MapJS.json(normalize(recipe, map));

					if (!(recipe instanceof JsonRecipeJS)) {
						recipe.serializeInputs = true;
						recipe.serializeOutputs = true;
						recipe.deserializeJson();
					}

					return event.addRecipe(recipe, type, args);
				}
			}

			var recipe = type.factory.get();
			RecipeArguments args = new RecipeArguments(recipe, args1);
			recipe.type = type;
			recipe.json = new JsonObject();
			recipe.serializeInputs = true;
			recipe.serializeOutputs = true;
			recipe.create(args);
			return event.addRecipe(recipe, type, args);
		} catch (RecipeExceptionJS ex) {
			ex.error();
			ConsoleJS.SERVER.error("Failed to create recipe for type '" + typeID + "'", ex, SKIP_ERROR);
		} catch (Throwable ex) {
			ConsoleJS.SERVER.handleError(ex, SKIP_ERROR, "Failed to create recipe for type '" + typeID + "' with args " + Arrays.toString(args0));
		}

		return new JsonRecipeJS();
	}

	private Object normalize(RecipeJS recipe, Object o) {
		if (o instanceof ItemStack stack) {
			return recipe.itemToJson(stack);
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
				map.put(entry.getKey(), normalize(recipe, entry.getValue()));
			}

			return map;
		} else if (o instanceof Iterable<?> itr) {
			var list = new ArrayList<>();

			for (var o1 : itr) {
				list.add(normalize(recipe, o1));
			}

			return list;
		}

		return o;
	}

	@Override
	public String toString() {
		return typeID.toString();
	}
}