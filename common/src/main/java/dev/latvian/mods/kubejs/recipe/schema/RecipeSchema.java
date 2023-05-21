package dev.latvian.mods.kubejs.recipe.schema;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.recipe.RecipeFunction;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.OptionalRecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.util.JsonIO;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Supplier;

public class RecipeSchema {
	public final Class<? extends RecipeJS> recipeType;
	public final Supplier<? extends RecipeJS> factory;
	public final RecipeKey<?>[] keys;
	public final int[] inputKeys;
	public final int[] outputKeys;
	private int minRequiredArguments;
	private Map<Integer, RecipeConstructor> constructors;

	public RecipeSchema(RecipeKey<?>... keys) {
		this(RecipeJS.class, RecipeJS::new, keys);
	}

	public RecipeSchema(Class<? extends RecipeJS> recipeType, Supplier<? extends RecipeJS> factory, RecipeKey<?>... keys) {
		this.recipeType = recipeType;
		this.factory = factory;
		this.keys = keys;
		this.minRequiredArguments = 0;

		var inKeys = new IntArrayList(keys.length / 2);
		var outKeys = new IntArrayList(keys.length / 2);

		var set = new HashSet<String>();

		for (int i = 0; i < keys.length; i++) {
			if (!(keys[i].component() instanceof OptionalRecipeComponent)) {
				if (minRequiredArguments > 0) {
					throw new IllegalStateException("Required key '" + keys[i].name() + "' must be ahead of other default keys!");
				}
			} else {
				minRequiredArguments++;
			}

			if (!set.add(keys[i].name())) {
				throw new IllegalStateException("Duplicate key '" + keys[i].name() + "'");
			}

			if (keys[i].component().getType() == RecipeComponentType.INPUT) {
				inKeys.add(i);
			} else if (keys[i].component().getType() == RecipeComponentType.OUTPUT) {
				outKeys.add(i);
			}
		}

		inputKeys = inKeys.toIntArray();
		outputKeys = outKeys.toIntArray();
	}

	public RecipeSchema constructor(RecipeConstructor.Factory factory, RecipeKey<?>... keys) {
		var c = new RecipeConstructor(this, keys, factory);

		if (constructors == null) {
			constructors = new HashMap<>(keys.length - minRequiredArguments + 1);
		}

		if (constructors.put(c.keys().length, c) != null) {
			throw new IllegalStateException("Constructor with " + c.keys().length + " arguments already exists!");
		}

		return this;
	}

	public RecipeSchema constructor(RecipeKey<?>... keys) {
		return constructor(RecipeConstructor.Factory.DEFAULT, keys);
	}

	public Map<Integer, RecipeConstructor> constructors() {
		if (keys.length == 0) {
			return Map.of();
		}

		if (constructors == null) {
			constructors = new HashMap<>(keys.length - minRequiredArguments + 1);

			KubeJS.LOGGER.info("Generating constructors for " + factory.get().getClass().getName());

			for (int a = minRequiredArguments; a <= keys.length; a++) {
				KubeJS.LOGGER.info("> " + a);
			}

			constructors.put(keys.length, new RecipeConstructor(this, keys, RecipeConstructor.Factory.DEFAULT));
		}

		return constructors;
	}

	public int minRequiredArguments() {
		return minRequiredArguments;
	}

	public RecipeJS deserialize(RecipeFunction type, @Nullable ResourceLocation id, JsonObject json) {
		var r = factory.get();
		r.type = type;
		r.id = id;
		r.json = json;
		r.newRecipe = id == null;
		r.initValues(this);

		if (id != null && CommonProperties.get().debugInfo) {
			r.originalJson = (JsonObject) JsonIO.copy(json);
		}

		r.deserialize();
		r.setAllChanged(id == null);
		return r;
	}
}
