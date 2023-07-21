package dev.latvian.mods.kubejs.recipe.schema;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.RecipeTypeFunction;
import dev.latvian.mods.kubejs.util.JsonIO;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * A recipe schema is a set of keys that defines how a recipe is constructed
 * from both KubeJS scripts (through the {@link #constructors}) and JSON files
 * (using the {@link #deserialize(RecipeTypeFunction, ResourceLocation, JsonObject)} method).
 * <p>
 * The schema also defines a {@link #factory} in order to create a {@link RecipeJS} object that
 * implements serialization logic for certain types of inputs or outputs through for example
 * {@link RecipeJS#readInputItem(Object)}, post-load validation ({@link RecipeJS#afterLoaded()}),
 * as well as entirely custom logic such as additional methods a developer may call from scripts.
 *
 * @see RecipeKey
 * @see RecipeJS
 */
public class RecipeSchema {
	private final UUID uuid;
	public final Class<? extends RecipeJS> recipeType;
	public final Supplier<? extends RecipeJS> factory;
	public final RecipeKey<?>[] keys;
	private int inputCount;
	private int outputCount;
	private int minRequiredArguments;
	private Int2ObjectMap<RecipeConstructor> constructors;

	/**
	 * Default constructor that uses {@link RecipeJS} as the default recipe factory.
	 *
	 * @param keys The keys that define this schema.
	 * @see #RecipeSchema(Class, Supplier, RecipeKey...)
	 */
	public RecipeSchema(RecipeKey<?>... keys) {
		this(RecipeJS.class, RecipeJS::new, keys);
	}

	/**
	 * Defines a new recipe schema that creates recipes of the given {@link RecipeJS} subclass.
	 * <p>
	 * Keys are defined in order of their appearance in the autogenerated constructor, where optional keys
	 * must be placed after all required keys.
	 *
	 * @param recipeType The type of recipe object this schema creates.
	 * @param factory    A factory to create a new instance of the recipe object. (This is passed to scripts)
	 * @param keys       The keys that define this schema.
	 */
	public RecipeSchema(Class<? extends RecipeJS> recipeType, Supplier<? extends RecipeJS> factory, RecipeKey<?>... keys) {
		this.uuid = UUID.randomUUID();
		this.recipeType = recipeType;
		this.factory = factory;
		this.keys = keys;
		this.minRequiredArguments = 0;
		this.inputCount = 0;
		this.outputCount = 0;

		var set = new HashSet<String>();

		for (int i = 0; i < keys.length; i++) {
			if (keys[i].optional()) {
				if (minRequiredArguments == 0) {
					minRequiredArguments = i;
				}
			} else if (minRequiredArguments > 0) {
				throw new IllegalArgumentException("Required key '" + keys[i].name + "' must be ahead of optional keys!");
			}

			if (!set.add(keys[i].name)) {
				throw new IllegalArgumentException("Duplicate key '" + keys[i].name + "' found!");
			}

			if (keys[i].component.role().isInput()) {
				inputCount++;
			} else if (keys[i].component.role().isOutput()) {
				outputCount++;
			}

			if (keys[i].alwaysWrite && keys[i].optional() && keys[i].optional.isDefault()) {
				throw new IllegalArgumentException("Key '" + keys[i] + "' can't have alwaysWrite() enabled with defaultOptional()!");
			}
		}

		if (minRequiredArguments == 0) {
			minRequiredArguments = keys.length;
		}
	}

	public UUID uuid() {
		return uuid;
	}

	/**
	 * Defines an additional constructor to be for this schema.
	 *
	 * @param factory The factory that is used to populate the recipe object with data after it is created.
	 * @param keys    The arguments that this constructor takes in.
	 * @return This schema.
	 * @implNote If a constructor is manually defined using this method, constructors will not be automatically generated.
	 */
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
			var keys1 = Arrays.stream(keys).filter(RecipeKey::includeInAutoConstructors).toArray(RecipeKey[]::new);

			constructors = keys1.length == 0 ? new Int2ObjectArrayMap<>() : new Int2ObjectArrayMap<>(keys1.length - minRequiredArguments + 1);
			boolean dev = DevProperties.get().debugInfo;

			if (dev) {
				KubeJS.LOGGER.info("Generating constructors for " + new RecipeConstructor(this, keys1, RecipeConstructor.Factory.DEFAULT));
			}

			for (int a = minRequiredArguments; a <= keys1.length; a++) {
				var k = new RecipeKey<?>[a];
				System.arraycopy(keys1, 0, k, 0, a);
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

	public int inputCount() {
		return inputCount;
	}

	public int outputCount() {
		return outputCount;
	}

	public RecipeJS deserialize(RecipeTypeFunction type, @Nullable ResourceLocation id, JsonObject json) {
		var r = factory.get();
		r.type = type;
		r.id = id;
		r.json = json;
		r.newRecipe = id == null;
		r.initValues(id == null);

		if (id != null && DevProperties.get().debugInfo) {
			r.originalJson = (JsonObject) JsonIO.copy(json);
		}

		r.deserialize(false);
		return r;
	}
}