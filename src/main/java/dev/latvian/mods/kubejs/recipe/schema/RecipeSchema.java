package dev.latvian.mods.kubejs.recipe.schema;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.RecipeTypeFunction;
import dev.latvian.mods.kubejs.recipe.component.UniqueIdBuilder;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.JsonUtils;
import dev.latvian.mods.rhino.util.RemapForJS;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SequencedCollection;

/**
 * A recipe schema is a set of keys that defines how a recipe is constructed
 * from both KubeJS scripts (through the {@link #constructors}) and JSON files
 * (using the {@link #deserialize(SourceLine, RecipeTypeFunction, ResourceLocation, JsonObject)} method).
 * <p>
 * The schema also defines a {@link #recipeFactory} in order to create a {@link KubeRecipe} object that
 * implements serialization logic, post-load validation ({@link KubeRecipe#afterLoaded()}),
 * as well as entirely custom logic such as additional methods a developer may call from scripts.
 *
 * @see RecipeKey
 * @see KubeRecipe
 */
public class RecipeSchema {
	public KubeRecipeFactory recipeFactory;
	public ResourceLocation typeOverride;
	public final List<RecipeKey<?>> keys;
	public final List<RecipeKey<?>> includedKeys;
	public final Map<RecipeKey<?>, RecipeOptional<?>> keyOverrides;
	public final Map<String, RecipeSchemaFunction> functions;
	private int inputCount;
	private int outputCount;
	private int minRequiredArguments;
	private Int2ObjectMap<RecipeConstructor> constructors;
	private boolean constructorsGenerated;
	private List<RecipeKey<?>> uniqueIds;
	boolean hidden;

	/**
	 * Defines a new recipe schema that creates recipes of the given {@link KubeRecipe} subclass.
	 * <p>
	 * Keys are defined in order of their appearance in the autogenerated constructor, where optional keys
	 * must be placed after all required keys.
	 *
	 * @param keys The keys that define this schema.
	 */
	public RecipeSchema(Map<RecipeKey<?>, RecipeOptional<?>> keyOverrides, List<RecipeKey<?>> keys) {
		this.recipeFactory = KubeRecipeFactory.DEFAULT;
		this.typeOverride = null;
		this.keys = List.copyOf(keys);
		this.keyOverrides = Map.copyOf(keyOverrides);
		this.includedKeys = List.copyOf(this.keys.stream().filter(k -> (k.optional == null || !k.excluded) && !this.keyOverrides.containsKey(k)).toList());
		this.functions = new LinkedHashMap<>(0);
		this.minRequiredArguments = 0;
		this.inputCount = 0;
		this.outputCount = 0;

		var set = new HashSet<String>();

		for (int i = 0; i < includedKeys.size(); i++) {
			var k = includedKeys.get(i);

			if (k.optional()) {
				if (minRequiredArguments == 0) {
					minRequiredArguments = i;
				}
			} else if (minRequiredArguments > 0) {
				throw new IllegalArgumentException("Required key '" + k.name + "' must be ahead of optional keys!");
			}

			if (!set.add(k.name)) {
				throw new IllegalArgumentException("Duplicate key '" + k.name + "' found!");
			}

			if (k.role.isInput()) {
				inputCount++;
			} else if (k.role.isOutput()) {
				outputCount++;
			}

			if (k.alwaysWrite && k.optional() && k.optional.isDefault()) {
				throw new IllegalArgumentException("Key '" + k + "' can't have alwaysWrite() enabled with defaultOptional()!");
			}
		}

		if (minRequiredArguments == 0) {
			minRequiredArguments = includedKeys.size();
		}

		this.uniqueIds = List.of();
		this.hidden = false;
	}

	public RecipeSchema(RecipeKey<?>... keys) {
		this(Map.of(), List.of(keys));
	}

	public RecipeSchema factory(KubeRecipeFactory factory) {
		this.recipeFactory = factory;
		return this;
	}

	public RecipeSchema typeOverride(ResourceLocation id) {
		this.typeOverride = id;
		return this;
	}

	public RecipeSchema constructor(RecipeConstructor constructor) {
		if (constructors == null) {
			constructors = new Int2ObjectArrayMap<>(keys.size() - minRequiredArguments() + 1);
		}

		if (constructors.put(constructor.keys.size(), constructor) != null) {
			throw new IllegalStateException("Constructor with " + constructor.keys.size() + " arguments already exists!");
		}

		return this;
	}

	/**
	 * Defines an additional constructor to be for this schema.
	 *
	 * @param keys The arguments that this constructor takes in.
	 * @return This schema.
	 * @implNote If a constructor is manually defined using this method, constructors will not be automatically generated.
	 */
	@RemapForJS("addConstructor") // constructor is a reserved word in TypeScript, so remap this for scripters who use .d.ts files for typing hints
	public RecipeSchema constructor(RecipeKey<?>... keys) {
		return constructor(new RecipeConstructor(keys));
	}

	public RecipeSchema uniqueId(RecipeKey<?> key) {
		uniqueIds = List.of(key);
		return this;
	}

	public RecipeSchema uniqueIds(SequencedCollection<RecipeKey<?>> keys) {
		uniqueIds = List.copyOf(keys);
		return this;
	}

	@Nullable
	public String buildUniqueId(KubeRecipe r) {
		if (uniqueIds.isEmpty()) {
			return null;
		} else if (uniqueIds.size() == 1) {
			var key = uniqueIds.getFirst();
			var value = r.getValue(key);

			if (value != null) {
				var builder = new UniqueIdBuilder(new StringBuilder());
				key.component.buildUniqueId(builder, Cast.to(value));
				return builder.build();
			}

			return null;
		} else {
			var sb = new StringBuilder();
			var builder = new UniqueIdBuilder(new StringBuilder());
			boolean first = true;

			for (var key : keys) {
				var value = r.getValue(key);

				if (value != null) {
					key.component.buildUniqueId(builder, Cast.to(value));
					var result = builder.build();

					if (result != null) {
						if (first) {
							first = false;
						} else {
							sb.append('/');
						}

						sb.append(result);
					}
				}
			}

			return sb.isEmpty() ? null : sb.toString();
		}
	}

	public Int2ObjectMap<RecipeConstructor> constructors() {
		if (constructors == null) {
			constructorsGenerated = true;
			constructors = includedKeys.isEmpty() ? new Int2ObjectArrayMap<>() : new Int2ObjectArrayMap<>(includedKeys.size() - minRequiredArguments + 1);
			boolean dev = DevProperties.get().logRecipeDebug;

			if (dev) {
				KubeJS.LOGGER.info("Generating constructors for " + new RecipeConstructor(includedKeys));
			}

			for (int a = minRequiredArguments; a <= includedKeys.size(); a++) {
				var c = new RecipeConstructor(List.copyOf(includedKeys.subList(0, a)));
				constructors.put(a, c);

				if (dev) {
					KubeJS.LOGGER.info("> " + a + ": " + c);
				}
			}
		}

		return constructors;
	}

	public List<RecipeKey<?>> uniqueIds() {
		return uniqueIds;
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

	public boolean isHidden() {
		return hidden;
	}

	public boolean constructorsGenerated() {
		constructors();
		return constructorsGenerated;
	}

	public KubeRecipe deserialize(SourceLine sourceLine, RecipeTypeFunction type, @Nullable ResourceLocation id, JsonObject json) {
		var r = recipeFactory.create(type, sourceLine, id == null);
		r.id = id;
		r.json = json;
		r.newRecipe = id == null;
		r.originalJson = json == null || id == null ? null : (JsonObject) JsonUtils.copy(json);
		r.deserialize(false);
		return r;
	}

	public RecipeSchema function(String name, RecipeSchemaFunction function) {
		this.functions.put(name, function);
		return this;
	}

	public <T> RecipeSchema setOpFunction(String name, RecipeKey<T> key, T value) {
		return function(name, new RecipeSchemaFunction.SetFunction<>(key, value));
	}

	public <T> RecipeSchema addToListOpFunction(String name, RecipeKey<List<T>> key) {
		return function(name, new RecipeSchemaFunction.AddToListFunction<>(key));
	}

	public <T> RecipeKey<T> getKey(String id) {
		for (var key : keys) {
			if (key.name.equals(id)) {
				return (RecipeKey<T>) key;
			}
		}

		throw new NullPointerException("Key '" + id + "' not found");
	}
}
