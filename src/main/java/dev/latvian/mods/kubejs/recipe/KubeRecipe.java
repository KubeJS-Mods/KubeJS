package dev.latvian.mods.kubejs.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.core.RecipeLikeKJS;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.error.MissingComponentException;
import dev.latvian.mods.kubejs.error.RecipeComponentException;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.StringUtilsWrapper;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentValue;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentValueMap;
import dev.latvian.mods.kubejs.recipe.component.RecipeValidationContext;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.ingredientaction.ConsumeAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.CustomIngredientAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.DamageAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientActionHolder;
import dev.latvian.mods.kubejs.recipe.ingredientaction.KeepAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.ReplaceAction;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.recipe.special.KubeJSCraftingRecipe;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.ErrorStack;
import dev.latvian.mods.kubejs.util.KubeResourceLocation;
import dev.latvian.mods.kubejs.util.SlotFilter;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.CustomJavaToJsWrapper;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KubeRecipe implements RecipeLikeKJS, CustomJavaToJsWrapper {
	public static final String CHANGED_MARKER = "_kubejs_changed_marker";
	public static final TypeInfo TYPE_INFO = TypeInfo.of(KubeRecipe.class);

	public ResourceLocation id;
	public RecipeTypeFunction type;
	public boolean newRecipe;
	public boolean removed;
	public SourceLine sourceLine = SourceLine.UNKNOWN;
	public String modifyResult = "";

	private RecipeComponentValueMap valueMap = RecipeComponentValueMap.EMPTY;
	private RecipeComponentValue<?>[] inputValues;
	private RecipeComponentValue<?>[] outputValues;

	public JsonObject originalJson = null;
	private MutableObject<Recipe<?>> originalRecipe = null;
	public JsonObject json = null;
	public boolean changed = false;
	public boolean creationError = false;

	protected List<IngredientActionHolder> recipeIngredientActions;

	@Override
	public final Scriptable convertJavaToJs(Context cx, Scriptable scope, TypeInfo staticType) {
		return new RecipeFunction(cx, scope, staticType, this);
	}

	public void deserialize(boolean merge) {
		for (var v : valueMap.holders) {
			try {
				v.key.component.readFromJson(this, Cast.to(v), json);
			} catch (Exception ex) {
				if (v.key.optional()) {
					ConsoleJS.SERVER.warn("Failed to read component '%s' from recipe %s, falling back to default value".formatted(v.key, this), sourceLine, ex, RecipesKubeEvent.POST_SKIP_ERROR);
				} else {
					throw new RecipeComponentException("Failed to read required component '%s'".formatted(v.key), ex, v).source(sourceLine);
				}
			}

			if (v.value != null) {
				if (merge) {
					v.write();
				}
			} else if (!v.key.optional()) {
				throw new MissingComponentException(v.key.name, v.key, valueMap.keySet()).source(sourceLine);
			}
		}
	}

	public void serialize() {
		for (var v : valueMap.holders) {
			if (v.shouldWrite()) {
				if (v.value == null) {
					throw new KubeRuntimeException("Value not set for " + v.key + " in recipe " + this).source(sourceLine);
				}

				v.key.component.writeToJson(this, Cast.to(v), json);
			}
		}
	}

	@Nullable
	public <T> T getValue(RecipeKey<T> key) {
		var v = valueMap.getHolder(key);

		if (v == null) {
			throw new MissingComponentException(key.name, key, valueMap.keySet()).source(sourceLine);
		}

		return Cast.to(v.value);
	}

	public <T> KubeRecipe setValue(RecipeKey<T> key, T value) {
		RecipeComponentValue<T> v = Cast.to(valueMap.getHolder(key));

		if (v == null) {
			throw new MissingComponentException(key.name, key, valueMap.keySet()).source(sourceLine);
		}

		v.value = value;
		v.write();
		save();
		return this;
	}

	// intended for use by scripts
	@Nullable
	public Object get(String key) {
		for (var h : valueMap.holders) {
			for (var name : h.key.names) {
				if (name.equals(key)) {
					return h.value;
				}
			}
		}

		throw new MissingComponentException(key, null, valueMap.keySet()).source(sourceLine);
	}

	// intended for use by scripts
	public KubeRecipe set(Context cx, String key, Object value) {
		for (var h : valueMap.holders) {
			for (var name : h.key.names) {
				if (name.equals(key)) {
					var errors = new ErrorStack();
					h.value = Cast.to(h.key.component.wrap(new RecipeScriptContext.Impl(cx, this, errors), Wrapper.unwrapped(value)));
					h.write();
					save();
					return this;
				}
			}
		}

		throw new MissingComponentException(key, null, valueMap.keySet()).source(sourceLine);
	}

	public void initValues(boolean save) {
		if (save) {
			save();
		}

		if (!type.schemaType.schema.keys.isEmpty()) {
			valueMap = new RecipeComponentValueMap(type.schemaType.schema.keys);

			if (save) {
				for (var v : valueMap.holders) {
					if (v.key.optional()) {
						v.value = Cast.to(v.key.optional.getDefaultValue(type.schemaType));
					}

					if (v.key.alwaysWrite) {
						v.write();
					}
				}
			}
		}
	}

	@HideFromJS
	public RecipeComponentValue<?>[] getRecipeComponentValues() {
		return valueMap.holders;
	}

	public final void afterLoaded(ErrorStack stack) {
		afterLoaded(new RecipeValidationContext.Impl(this, stack));
	}

	public final void afterLoaded(RecipeValidationContext cx) {
		cx.errors().push(this);

		var postProcessors = type.schemaType.schema.postProcessors();

		if (!postProcessors.isEmpty()) {
			cx.errors().push("Post Processors");

			for (int i = 0; i < postProcessors.size(); i++) {
				cx.errors().setKey(i);
				postProcessors.get(i).process(cx, this);
			}

			cx.errors().pop();
		}

		for (var v : valueMap.holders) {
			cx.errors().setKey(v.key.name);
			v.validate(cx, sourceLine);
		}

		validate(cx);
		cx.errors().pop();
	}

	/**
	 * Perform additional validation after the recipe has been loaded.
	 */
	public void validate(RecipeValidationContext cx) {
	}

	public final void save() {
		changed = true;
	}

	public KubeRecipe id(KubeResourceLocation id) {
		this.id = id.wrapped();
		save();
		return this;
	}

	public KubeRecipe group(String g) {
		kjs$setGroup(g);
		return this;
	}

	public KubeRecipe merge(JsonObject j) {
		if (j != null && j.size() > 0) {
			for (var entry : j.entrySet()) {
				json.add(entry.getKey(), entry.getValue());
			}

			save();
			deserialize(true);
		}

		return this;
	}

	public final boolean hasChanged() {
		if (changed) {
			return true;
		}

		for (var vc : valueMap.holders) {
			if (vc.shouldWrite()) {
				return true;
			}
		}

		return false;
	}

	// RecipeLikeKJS methods //

	@Override
	public ResourceKey<RecipeSerializer<?>> kjs$getTypeKey() {
		return type.serializerKey;
	}

	@Override
	@Deprecated
	public final String kjs$getGroup() {
		var e = json.get("group");
		return e instanceof JsonPrimitive ? e.getAsString() : "";
	}

	@Override
	@Deprecated
	@HideFromJS
	public final void kjs$setGroup(String group) {
		if (!kjs$getGroup().equals(group)) {
			if (group.isEmpty()) {
				json.remove("group");
			} else {
				json.addProperty("group", group);
			}

			save();
		}
	}

	@Override
	@Deprecated
	public final ResourceLocation kjs$getOrCreateId() {
		return getOrCreateId();
	}

	@Override
	@Deprecated
	public final RecipeSchema kjs$getSchema(Context cx) {
		return type.schemaType.schema;
	}

	@Override
	public RecipeSerializer<?> kjs$getSerializer() {
		return type.schemaType.getSerializer();
	}

	@SuppressWarnings({"SuspiciousToArrayCall", "ToArrayCallWithZeroLengthArrayArgument"})
	public final RecipeComponentValue<?>[] inputValues() {
		if (inputValues == null) {
			if (type.schemaType.schema.inputCount() == 0) {
				inputValues = Cast.to(RecipeComponentValue.EMPTY_ARRAY);
			} else {
				var list = new ArrayList<>(type.schemaType.schema.inputCount());

				for (var v : valueMap.holders) {
					if (v.key.role.isInput()) {
						list.add(v);
					}
				}

				inputValues = list.toArray(new RecipeComponentValue[list.size()]);
			}
		}

		return inputValues;
	}

	@SuppressWarnings({"SuspiciousToArrayCall", "ToArrayCallWithZeroLengthArrayArgument"})
	public final RecipeComponentValue<?>[] outputValues() {
		if (outputValues == null) {
			if (type.schemaType.schema.outputCount() == 0) {
				outputValues = Cast.to(RecipeComponentValue.EMPTY_ARRAY);
			} else {
				var list = new ArrayList<>(type.schemaType.schema.outputCount());

				for (var v : valueMap.holders) {
					if (v.key.role.isOutput()) {
						list.add(v);
					}
				}

				outputValues = list.toArray(new RecipeComponentValue[list.size()]);
			}
		}

		return outputValues;
	}

	@Override
	public boolean hasInput(RecipeMatchContext cx, ReplacementMatchInfo match) {
		for (var v : inputValues()) {
			if (v.matches(cx, match)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean replaceInput(RecipeScriptContext cx, ReplacementMatchInfo match, Object with) {
		boolean replaced = false;

		for (var v : inputValues()) {
			replaced = v.replace(cx, match, with) || replaced;
		}

		if (replaced) {
			save();
		}

		return replaced;
	}

	@Override
	public boolean hasOutput(RecipeMatchContext cx, ReplacementMatchInfo match) {
		for (var v : outputValues()) {
			if (v.matches(cx, match)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean replaceOutput(RecipeScriptContext cx, ReplacementMatchInfo match, Object with) {
		boolean replaced = false;

		for (var v : outputValues()) {
			replaced = v.replace(cx, match, with) || replaced;
		}

		if (replaced) {
			save();
		}

		return replaced;
	}

	@Override
	public String toString() {
		if (id == null && json == null) {
			return "<no id> [" + type + "]";
		}

		return getOrCreateId() + "[" + type + "]";
	}

	public String getId() {
		return getOrCreateId().toString();
	}

	public String getPath() {
		return getOrCreateId().getPath();
	}

	@HideFromJS
	public ResourceLocation getOrCreateId() {
		if (id == null) {
			var js = getSerializationTypeFunction();
			var ids = CommonProperties.get().ignoreCustomUniqueRecipeIds ? null : js.schemaType.schema.buildUniqueId(this);

			var prefix = js.id.getNamespace() + ":kjs/";

			if (ids == null || ids.isEmpty()) {
				ids = StringUtilsWrapper.getUniqueId(json);
			} else {
				ids = ids.replace(':', '_');
			}

			id = type.event.takeId(this, prefix, ids);
		}

		return id;
	}

	public String getFromToString() {
		var sb = new StringBuilder();
		sb.append('[');

		for (var v : inputValues()) {
			if (sb.length() > 1) {
				sb.append(",");
			}

			sb.append(v.value);
		}

		sb.append("] -> [");

		for (var v : outputValues()) {
			if (sb.length() > 1) {
				sb.append(",");
			}

			sb.append(v.value);
		}

		return sb.append(']').toString();
	}

	public final void remove() {
		if (!removed) {
			removed = true;

			if (DevProperties.get().logRemovedRecipes) {
				ConsoleJS.SERVER.info("- " + this + ": " + getFromToString());
			} else if (ConsoleJS.SERVER.shouldPrintDebug()) {
				ConsoleJS.SERVER.debug("- " + this + ": " + getFromToString());
			}
		}
	}

	public KubeRecipe stage(String s) {
		json.addProperty(KubeJSCraftingRecipe.STAGE_KEY, s);
		save();
		return this;
	}

	/**
	 * Only used by {@link KubeRecipe#getOrCreateId()} and {@link KubeRecipe#serializeChanges()} in rare case that a recipe can be another recipe type than itself (e.g. kubejs:shaped -> minecraft:crafting_shaped)
	 */
	public RecipeTypeFunction getSerializationTypeFunction() {
		return type;
	}

	public KubeRecipe serializeChanges() {
		if (newRecipe || hasChanged()) {
			serialize();

			if (!modifyResult.isEmpty()) {
				json.addProperty(KubeJSCraftingRecipe.MODIFY_RESULT_KEY, modifyResult);
			}

			if (recipeIngredientActions != null && !recipeIngredientActions.isEmpty()) {
				try {
					json.add(KubeJSCraftingRecipe.INGREDIENT_ACTIONS_KEY, IngredientActionHolder.LIST_CODEC.encodeStart(type.event.ops.json(), recipeIngredientActions).getOrThrow());
				} catch (Throwable ex) {
					ConsoleJS.SERVER.error("Failed to encode " + KubeJSCraftingRecipe.INGREDIENT_ACTIONS_KEY, sourceLine, ex, RecipesKubeEvent.CREATE_RECIPE_SKIP_ERROR);
				}
			}

			if (newRecipe) {
				json.addProperty("type", getSerializationTypeFunction().idString);
			}

			if (type.event.stageSerializer != null && json.has(KubeJSCraftingRecipe.STAGE_KEY) && !type.idString.equals("recipestages:stage")) {
				var staged = new JsonObject();
				staged.addProperty("stage", json.get(KubeJSCraftingRecipe.STAGE_KEY).getAsString());
				staged.add("recipe", json);
				json = staged;
			}

			json.addProperty(CHANGED_MARKER, true);
		}

		return this;
	}

	@Nullable
	public Recipe<?> getOriginalRecipe() {
		if (originalRecipe == null) {
			originalRecipe = new MutableObject<>();
			try {
				var serializer = type.schemaType.getSerializer();
				var ops = type.event.ops.json();

				// people apparently violate the contract here?!
				//noinspection OptionalOfNullableMisuse
				Optional.ofNullable(serializer.codec())
					.map(DataResult::success)
					.orElseGet(() -> DataResult.error(() -> "Codec for " + serializer.getClass().getName() + " is null!"))
					.flatMap(codec -> ops.getMap(json).flatMap(map -> codec.decode(ops, map)))
					.mapError(err -> "Error parsing recipe " + id + ": " + err)
					.ifSuccess(originalRecipe::setValue)
					.ifError(err -> {
						if (DevProperties.get().logErroringParsedRecipes) {
							ConsoleJS.SERVER.error(err.message());
						} else {
							RecipeManager.LOGGER.error(err.message());
						}
					});
			} catch (Throwable e) {
				ConsoleJS.SERVER.error("Could not create recipe from json for " + this, e);
			}
		}

		return originalRecipe.getValue();
	}

	public ItemStack getOriginalRecipeResult() {
		var original = getOriginalRecipe();

		if (original == null) {
			ConsoleJS.SERVER.warn("Original recipe is null - could not get result");
			return ItemStack.EMPTY;
		}

		var result = original.getResultItem(type.event.registries.access());
		//noinspection ConstantValue
		return result == null ? ItemStack.EMPTY : result;
	}

	public List<Ingredient> getOriginalRecipeIngredients() {
		var original = getOriginalRecipe();

		if (original == null) {
			ConsoleJS.SERVER.warn("Original recipe is null - could not get ingredients");
			return List.of();
		}

		return List.copyOf(original.getIngredients());
	}

	public KubeRecipe ingredientAction(SlotFilter filter, IngredientAction action) {
		if (recipeIngredientActions == null) {
			recipeIngredientActions = new ArrayList<>(2);
		}

		recipeIngredientActions.add(new IngredientActionHolder(action, filter));
		save();
		return this;
	}

	public final KubeRecipe damageIngredient(SlotFilter filter, int damage) {
		return ingredientAction(filter, new DamageAction(damage));
	}

	public final KubeRecipe damageIngredient(SlotFilter filter) {
		return damageIngredient(filter, 1);
	}

	public final KubeRecipe replaceIngredient(SlotFilter filter, ItemStack item) {
		return ingredientAction(filter, new ReplaceAction(item));
	}

	public final KubeRecipe customIngredientAction(SlotFilter filter, String id) {
		return ingredientAction(filter, new CustomIngredientAction(id));
	}

	public final KubeRecipe keepIngredient(SlotFilter filter) {
		return ingredientAction(filter, KeepAction.INSTANCE);
	}

	public final KubeRecipe consumeIngredient(SlotFilter filter) {
		return ingredientAction(filter, ConsumeAction.INSTANCE);
	}

	public final KubeRecipe modifyResult(String id) {
		modifyResult = id;
		save();
		return this;
	}
}