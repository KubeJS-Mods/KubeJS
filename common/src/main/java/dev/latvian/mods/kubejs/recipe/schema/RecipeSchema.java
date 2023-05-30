package dev.latvian.mods.kubejs.recipe.schema;

import com.google.gson.JsonObject;
import dev.architectury.platform.Platform;
import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.RecipeTypeFunction;
import dev.latvian.mods.kubejs.util.JsonIO;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.UUID;
import java.util.function.Supplier;

public class RecipeSchema {
	private final UUID uuid;
	public final Class<? extends RecipeJS> recipeType;
	public final Supplier<? extends RecipeJS> factory;
	public final RecipeKey<?>[] keys;
	public final int[] inputKeys;
	public final int[] outputKeys;
	private int minRequiredArguments;
	private Int2ObjectMap<RecipeConstructor> constructors;

	public RecipeSchema(RecipeKey<?>... keys) {
		this(RecipeJS.class, RecipeJS::new, keys);
	}

	public RecipeSchema(Class<? extends RecipeJS> recipeType, Supplier<? extends RecipeJS> factory, RecipeKey<?>... keys) {
		this.uuid = UUID.randomUUID();
		this.recipeType = recipeType;
		this.factory = factory;
		this.keys = keys;
		this.minRequiredArguments = 0;

		var inKeys = new IntArrayList(keys.length / 2);
		var outKeys = new IntArrayList(keys.length / 2);

		var set = new HashSet<String>();

		for (int i = 0; i < keys.length; i++) {
			keys[i].index(i);

			if (keys[i].optional() != null) {
				if (minRequiredArguments == 0) {
					minRequiredArguments = i;
				}
			} else if (minRequiredArguments > 0) {
				throw new IllegalStateException("Required key '" + keys[i].name() + "' must be ahead of optional keys!");
			}

			if (!set.add(keys[i].name())) {
				throw new IllegalStateException("Duplicate key '" + keys[i].name() + "' found!");
			}

			if (keys[i].component().role().isInput()) {
				inKeys.add(i);
			} else if (keys[i].component().role().isOutput()) {
				outKeys.add(i);
			}
		}

		if (minRequiredArguments == 0) {
			minRequiredArguments = keys.length;
		}

		inputKeys = inKeys.toIntArray();
		outputKeys = outKeys.toIntArray();
	}

	public UUID uuid() {
		return uuid;
	}

	public RecipeSchema constructor(RecipeConstructor.Factory factory, RecipeKey<?>... keys) {
		var c = new RecipeConstructor(this, keys, factory);

		if (constructors == null) {
			constructors = new Int2ObjectArrayMap<>(keys.length - minRequiredArguments + 1);
		}

		if (constructors.put(c.keys().length, c) != null) {
			throw new IllegalStateException("Constructor with " + c.keys().length + " arguments already exists!");
		}

		return this;
	}

	public RecipeSchema constructor(RecipeKey<?>... keys) {
		return constructor(RecipeConstructor.Factory.DEFAULT, keys);
	}

	public Int2ObjectMap<RecipeConstructor> constructors() {
		if (constructors == null) {
			constructors = keys.length == 0 ? new Int2ObjectArrayMap<>() : new Int2ObjectArrayMap<>(keys.length - minRequiredArguments + 1);
			boolean dev = Platform.isDevelopmentEnvironment();

			if (dev) {
				KubeJS.LOGGER.info("Generating constructors for " + new RecipeConstructor(this, keys, RecipeConstructor.Factory.DEFAULT));
			}

			for (int a = minRequiredArguments; a <= keys.length; a++) {
				var k = new RecipeKey<?>[a];
				System.arraycopy(keys, 0, k, 0, a);
				var c = new RecipeConstructor(this, k, RecipeConstructor.Factory.DEFAULT);
				constructors.put(a, c);

				if (dev) {
					KubeJS.LOGGER.info("> " + a + ": " + c);
				}
			}
		}

		return constructors;
	}

	public int minRequiredArguments() {
		return minRequiredArguments;
	}

	public RecipeJS deserialize(RecipeTypeFunction type, @Nullable ResourceLocation id, JsonObject json) {
		var r = factory.get();
		r.type = type;
		r.id = id;
		r.json = json;
		r.newRecipe = id == null;
		r.initValues(this);

		if (id != null && DevProperties.get().debugInfo) {
			r.originalJson = (JsonObject) JsonIO.copy(json);
		}

		r.deserialize();
		r.setAllChanged(id == null);
		return r;
	}
}
