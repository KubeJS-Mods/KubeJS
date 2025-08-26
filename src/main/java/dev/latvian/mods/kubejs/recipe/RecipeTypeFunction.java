package dev.latvian.mods.kubejs.recipe;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.recipe.component.ComponentValueMap;
import dev.latvian.mods.kubejs.recipe.component.ValidationContext;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaType;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.util.ErrorStack;
import dev.latvian.mods.kubejs.util.JsonUtils;
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
	public static final Pattern SKIP_ERROR = ConsoleJS.methodPattern(RecipeTypeFunction.class, "call");

	public final RecipesKubeEvent event;
	public final ResourceLocation id;
	public final String idString;
	public final RecipeSchemaType schemaType;

	public RecipeTypeFunction(RecipesKubeEvent event, RecipeSchemaType schemaType) {
		this.event = event;
		this.id = schemaType.id;
		this.idString = id.toString();
		this.schemaType = schemaType;
	}

	@Override
	public KubeRecipe call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args0) {
		var sourceLine = SourceLine.of(cx);
		var stack = new ErrorStack();

		try {
			return createRecipe(cx, sourceLine, stack, args0);
		} catch (Throwable cause) {
			var r = schemaType.schema.recipeFactory.create(this, sourceLine, true);
			r.creationError = true;
			event.failedCount++;
			ConsoleJS.SERVER.error("Failed to create a '" + idString + "' recipe" + stack.atString() + " from args " + Arrays.toString(args0), sourceLine, cause, SKIP_ERROR);
			r.json = new JsonObject();
			r.json.addProperty("type", idString);
			r.newRecipe = true;
			return r;
		}
	}

	public KubeRecipe createRecipe(Context cx, SourceLine sourceLine, ErrorStack stack, Object[] args) {
		try {
			for (int i = 0; i < args.length; i++) {
				args[i] = Wrapper.unwrapped(args[i]);
			}

			schemaType.getSerializer();

			var constructor = schemaType.schema.constructors().get(args.length);

			if (constructor == null) {
				if (args.length == 1 && (args[0] instanceof Map<?, ?> || args[0] instanceof JsonObject)) {
					var recipe = schemaType.schema.deserialize(sourceLine, this, null, JsonUtils.objectOf(cx, args[0]));
					recipe.afterLoaded(new ValidationContext(event, stack));
					return event.addRecipe(recipe, true);
					// throw new RecipeExceptionJS("Use event.custom(json) for json recipes!");
				}

				throw new KubeRuntimeException("Constructor for " + id + " with " + args.length + " arguments not found!").source(sourceLine);
			}

			/*
			if (args1 == null || args1.isEmpty()) {
				throw new RecipeExceptionJS("Recipe requires at least one argument!");
			} else if (schemaType.schema.keys.length == 0 && args1.size() != 1) {
				throw new RecipeExceptionJS("Custom recipe has to use a single json object argument!");
			}
			 */

			var argMap = new ComponentValueMap(args.length);
			int index = 0;

			for (var key : constructor.keys) {
				argMap.put(key, Wrapper.unwrapped(args[index++]));
			}

			var recipe = constructor.create(cx, sourceLine, this, schemaType, argMap);
			recipe.afterLoaded(new ValidationContext(event, stack));
			return event.addRecipe(recipe, false);
		} catch (KubeRuntimeException rex) {
			throw rex.source(sourceLine);
		} catch (Throwable ex) {
			throw new KubeRuntimeException("Failed to create a recipe for type '" + id + "'" + stack.atString() + " with args " + Arrays.stream(args).map(o -> o == null ? "null" : (o + ": " + o.getClass().getSimpleName())).collect(Collectors.joining(", ", "[", "]")), ex).source(sourceLine);
		}
	}

	@Override
	public String toString() {
		return idString;
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