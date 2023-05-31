package dev.latvian.mods.kubejs.recipe;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.recipe.component.ComponentValueMap;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaType;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.WrappedJS;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.Wrapper;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RecipeTypeFunction extends BaseFunction implements WrappedJS {
	private static final Pattern SKIP_ERROR = Pattern.compile("at\\s+dev\\.latvian\\.mods\\.kubejs\\.recipe\\.RecipeTypeFunction\\.call");

	public final RecipesEventJS event;
	public final ResourceLocation id;
	public final String idString;
	public final RecipeSchemaType schemaType;

	public RecipeTypeFunction(RecipesEventJS event, RecipeSchemaType schemaType) {
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
			schemaType.getSerializer();

			var args1 = ListJS.of(args0);

			if (args1 == null || args1.isEmpty()) {
				throw new RecipeExceptionJS("Recipe requires at least one argument!");
			} else if (schemaType.schema.keys.length == 0 && args1.size() != 1) {
				throw new RecipeExceptionJS("Custom recipe has to use a single json object argument!");
			}

			var constructor = schemaType.schema.constructors().get(args1.size());

			if (constructor == null) {
				if (args0[0] instanceof Map<?, ?> || args0[0] instanceof JsonObject) {
					throw new RecipeExceptionJS("Use event.custom(json) for json recipes!");
				}

				throw new RecipeExceptionJS("Constructor for " + id + " with " + args1.size() + " arguments not found!");
			}

			var argMap = new ComponentValueMap(args1.size());
			int index = 0;

			for (var key : constructor.keys()) {
				argMap.put(key, Wrapper.unwrapped(args1.get(index++)));
			}

			var recipe = constructor.factory().create(this, schemaType, constructor.keys(), argMap);
			recipe.afterLoaded();
			return event.addRecipe(recipe, false);
		} catch (RecipeExceptionJS ex) {
			ex.error();
			ConsoleJS.SERVER.error("Failed to create recipe for type '" + id + "'", ex, SKIP_ERROR);
		} catch (Throwable ex) {
			ConsoleJS.SERVER.handleError(ex, SKIP_ERROR, "Failed to create recipe for type '" + id + "' with args " + Arrays.stream(args0).map(Wrapper::unwrapped).map(o -> o == null ? "null" : (o + ": " + o.getClass().getSimpleName())).collect(Collectors.joining(", ", "[", "]")));
		}

		return new JsonRecipeJS();
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
		return idString.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return idString.equals(obj.toString());
	}
}