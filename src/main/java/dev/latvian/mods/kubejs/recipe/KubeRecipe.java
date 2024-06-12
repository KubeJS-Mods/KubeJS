package dev.latvian.mods.kubejs.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.core.RecipeLikeKJS;
import dev.latvian.mods.kubejs.helpers.RecipeHelper;
import dev.latvian.mods.kubejs.recipe.component.MissingComponentException;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentBuilderMap;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentValue;
import dev.latvian.mods.kubejs.recipe.ingredientaction.ConsumeAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.CustomIngredientAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.DamageAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientActionHolder;
import dev.latvian.mods.kubejs.recipe.ingredientaction.KeepAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.ReplaceAction;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.recipe.special.KubeJSCraftingRecipe;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.SlotFilter;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.CustomJavaToJsWrapper;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.fml.loading.FMLLoader;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class KubeRecipe implements RecipeLikeKJS, CustomJavaToJsWrapper {
	public static boolean itemErrors = false;

	public ResourceLocation id;
	public RecipeTypeFunction type;
	public boolean newRecipe;
	public boolean removed;
	public String modifyResult = "";

	private RecipeComponentBuilderMap valueMap = RecipeComponentBuilderMap.EMPTY;
	private RecipeComponentValue<?>[] inputValues;
	private RecipeComponentValue<?>[] outputValues;

	public JsonObject originalJson = null;
	private MutableObject<Recipe<?>> originalRecipe = null;
	public JsonObject json = null;
	public boolean changed = false;

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
				ConsoleJS.SERVER.error("Failed to read " + v.key + " from recipe " + this, ex, RecipesKubeEvent.POST_SKIP_ERROR);
			}

			if (v.value != null) {
				if (merge) {
					v.write();
				}
			} else if (!v.key.optional()) {
				throw new MissingComponentException(v.key.name, v.key, valueMap.keySet());
			}
		}
	}

	public void serialize() {
		for (var v : valueMap.holders) {
			if (v.shouldWrite()) {
				if (v.value == null) {
					throw new RecipeExceptionJS("Value not set for " + v.key + " in recipe " + this);
				}

				v.key.component.writeToJson(this, Cast.to(v), json);
			}
		}
	}

	public <T> T getValue(RecipeKey<T> key) {
		var v = valueMap.getHolder(key);

		if (v == null) {
			throw new MissingComponentException(key.name, key, valueMap.keySet());
		}

		return Cast.to(v.value);
	}

	public <T> KubeRecipe setValue(RecipeKey<T> key, T value) {
		RecipeComponentValue<T> v = Cast.to(valueMap.getHolder(key));

		if (v == null) {
			throw new MissingComponentException(key.name, key, valueMap.keySet());
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

		throw new MissingComponentException(key, null, valueMap.keySet());
	}

	// intended for use by scripts
	public KubeRecipe set(Context cx, String key, Object value) {
		for (var h : valueMap.holders) {
			for (var name : h.key.names) {
				if (name.equals(key)) {
					h.value = Cast.to(h.key.component.wrap(cx, this, Wrapper.unwrapped(value)));
					h.write();
					save();
					return this;
				}
			}
		}

		throw new MissingComponentException(key, null, valueMap.keySet());
	}

	public void initValues(boolean created) {
		if (created) {
			save();
		}

		if (type.schemaType.schema.keys.length > 0) {
			valueMap = new RecipeComponentBuilderMap(type.schemaType.schema.keys);

			if (created) {
				for (var v : valueMap.holders) {
					if (v.key.alwaysWrite || !v.key.optional()) {
						if (v.key.alwaysWrite) {
							// FIXME? Not sure why read() was called here v.value = Cast.to(v.key.component.read(this, v.key.optional.getDefaultValue(type.schemaType)));
							v.value = Cast.to(v.key.optional.getDefaultValue(type.schemaType));
						}

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

	/**
	 * Perform additional validation after the recipe has been loaded.
	 * <p>
	 * You probably want to call <code>super.afterLoaded()</code> as well
	 * if you override this, in order to check for empty values.
	 */
	public void afterLoaded() {
		for (var v : valueMap.holders) {
			var e = v.checkEmpty();

			if (!e.isEmpty()) {
				throw new RecipeExceptionJS(e);
			}
		}
	}

	public final void save() {
		changed = true;
	}

	public KubeRecipe id(ResourceLocation _id) {
		id = _id;
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

	// RecipeKJS methods //

	@Override
	@Deprecated
	public final String kjs$getGroup() {
		var e = json.get("group");
		return e instanceof JsonPrimitive ? e.getAsString() : "";
	}

	@Override
	@Deprecated
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
	@Deprecated
	public final ResourceLocation kjs$getType() {
		return getType();
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
	public boolean hasInput(Context cx, ReplacementMatch match) {
		for (var v : inputValues()) {
			if (v.isInput(this, match)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean replaceInput(Context cx, ReplacementMatch match, InputReplacement with) {
		boolean replaced = false;

		for (var v : inputValues()) {
			replaced = v.replaceInput(cx, this, match, with) || replaced;
		}

		if (replaced) {
			save();
		}

		return replaced;
	}

	@Override
	public boolean hasOutput(Context cx, ReplacementMatch match) {
		for (var v : outputValues()) {
			if (v.isOutput(this, match)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean replaceOutput(Context cx, ReplacementMatch match, OutputReplacement with) {
		boolean replaced = false;

		for (var v : outputValues()) {
			replaced = v.replaceOutput(cx, this, match, with) || replaced;
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
	public ResourceLocation getType() {
		return type.id;
	}

	@HideFromJS
	public ResourceLocation getOrCreateId() {
		if (id == null) {
			var js = getSerializationTypeFunction();
			var ids = CommonProperties.get().ignoreCustomUniqueRecipeIds ? null : js.schemaType.schema.uniqueIdFunction.apply(this);

			var prefix = js.id.getNamespace() + ":kjs/";

			if (ids == null || ids.isEmpty()) {
				ids = UtilsJS.getUniqueId(json);
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
	 * Only used by {@link KubeRecipe#getOrCreateId()} and {@link KubeRecipe#createRecipe()} in rare case that a recipe can be another recipe type than itself (e.g. kubejs:shaped -> minecraft:crafting_shaped)
	 */
	public RecipeTypeFunction getSerializationTypeFunction() {
		return type;
	}

	@Nullable
	public RecipeHolder<?> createRecipe() {
		if (removed) {
			return null;
		}

		if (newRecipe || hasChanged()) {
			serialize();

			if (!modifyResult.isEmpty()) {
				json.addProperty(KubeJSCraftingRecipe.MODIFY_RESULT_KEY, modifyResult);
			}

			if (recipeIngredientActions != null && !recipeIngredientActions.isEmpty()) {
				try {
					json.add(KubeJSCraftingRecipe.INGREDIENT_ACTIONS_KEY, IngredientActionHolder.LIST_CODEC.encodeStart(type.event.registries.json(), recipeIngredientActions).getOrThrow());
				} catch (Exception ex) {
					ConsoleJS.SERVER.error("Failed to encode " + KubeJSCraftingRecipe.INGREDIENT_ACTIONS_KEY, ex, RecipesKubeEvent.CREATE_RECIPE_SKIP_ERROR);
				}
			}

			if (newRecipe) {
				json.addProperty("type", getSerializationTypeFunction().idString);
			}

			var id = getOrCreateId();

			if (type.event.stageSerializer != null && json.has(KubeJSCraftingRecipe.STAGE_KEY) && !type.idString.equals("recipestages:stage")) {
				var o = new JsonObject();
				o.addProperty("stage", json.get(KubeJSCraftingRecipe.STAGE_KEY).getAsString());
				o.add("recipe", json);
				var recipe = type.event.registries.decodeJson(type.event.stageSerializer.codec(), o);
				return new RecipeHolder<>(id, recipe);
			}
		} else if (originalRecipe != null) {
			return new RecipeHolder<>(getOrCreateId(), originalRecipe.getValue());
		}

		return RecipeHelper.get().fromJson(type.event.registries.json(), getSerializationTypeFunction().schemaType.getSerializer(), getOrCreateId(), json, !FMLLoader.isProduction());
	}

	@Nullable
	public Recipe<?> getOriginalRecipe() {
		Throwable error = new Throwable("Original recipe is null!");

		if (originalRecipe == null) {
			originalRecipe = new MutableObject<>();
			try {
				// todo: this sucks
				originalRecipe.setValue(RecipeHelper.get().fromJson(type.event.registries.json(), type.schemaType.getSerializer(), getOrCreateId(), json, !FMLLoader.isProduction()).value());
			} catch (Throwable e) {
				error = e;
			}

			if (originalRecipe == null) {
				if (KubeRecipe.itemErrors) {
					throw new RecipeExceptionJS("Could not create recipe from json for " + this, error);
				} else {
					ConsoleJS.SERVER.warn("Could not create recipe from json for " + this, error);
				}
			}
		}

		return originalRecipe.getValue();
	}

	public ItemStack getOriginalRecipeResult() {
		if (getOriginalRecipe() == null) {
			ConsoleJS.SERVER.warn("Original recipe is null - could not get result");
			return ItemStack.EMPTY;
		}

		var result = getOriginalRecipe().getResultItem(type.event.registries.access());
		//noinspection ConstantValue
		return result == null ? ItemStack.EMPTY : result;
	}

	public List<Ingredient> getOriginalRecipeIngredients() {
		if (getOriginalRecipe() == null) {
			ConsoleJS.SERVER.warn("Original recipe is null - could not get ingredients");
			return List.of();
		}

		return List.copyOf(getOriginalRecipe().getIngredients());
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
		return ingredientAction(filter, new KeepAction());
	}

	public final KubeRecipe consumeIngredient(SlotFilter filter) {
		return ingredientAction(filter, new ConsumeAction());
	}

	public final KubeRecipe modifyResult(String id) {
		modifyResult = id;
		save();
		return this;
	}
}