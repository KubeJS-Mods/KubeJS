package dev.latvian.mods.kubejs.recipe;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.item.ingredient.TagIngredientJS;
import dev.latvian.mods.kubejs.recipe.minecraft.CustomRecipeJS;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.kubejs.util.WrappedJS;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author LatvianModder
 */
public class RecipeFunction extends BaseFunction implements WrappedJS {
	private static final Pattern SKIP_ERROR = Pattern.compile("at dev.latvian.mods.kubejs.recipe.RecipeFunction.call");

	private final RecipeEventJS event;
	public final ResourceLocation typeID;
	public final RecipeTypeJS type;

	public RecipeFunction(RecipeEventJS e, ResourceLocation id, @Nullable RecipeTypeJS t) {
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
					recipe.json = MapJS.json(normalize(map));

					if (!(recipe instanceof CustomRecipeJS)) {
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
		} catch (Exception ex) {
			ConsoleJS.SERVER.printStackTrace(ex, SKIP_ERROR);
		}

		return new CustomRecipeJS();
	}

	private Object normalize(Object o) {
		if (o instanceof ItemStackJS stack) {
			return stack.toResultJson();
		} else if (o instanceof IngredientJS ingr) {
			return ingr.toJson();
		} else if (o instanceof String s) {
			if (s.length() >= 4 && s.startsWith("#") && s.indexOf(':') != -1) {
				return TagIngredientJS.createTag(s.substring(1)).toJson();
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
		return typeID.toString();
	}
}