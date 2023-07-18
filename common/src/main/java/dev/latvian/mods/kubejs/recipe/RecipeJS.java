package dev.latvian.mods.kubejs.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.util.UUIDTypeAdapter;
import dev.latvian.mods.kubejs.core.RecipeKJS;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.fluid.InputFluid;
import dev.latvian.mods.kubejs.fluid.OutputFluid;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.platform.RecipePlatformHelper;
import dev.latvian.mods.kubejs.recipe.component.MissingComponentException;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentValue;
import dev.latvian.mods.kubejs.recipe.ingredientaction.CustomIngredientAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.DamageAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientActionFilter;
import dev.latvian.mods.kubejs.recipe.ingredientaction.KeepAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.ReplaceAction;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.util.CustomJavaToJsWrapper;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RecipeJS implements RecipeKJS, CustomJavaToJsWrapper {
	public static boolean itemErrors = false;

	public ResourceLocation id;
	public RecipeTypeFunction type;
	public boolean newRecipe;
	public boolean removed;
	protected Map<RecipeKey<?>, RecipeComponentValue<?>> valueMap = Map.of();
	private RecipeComponentValue<?>[] inputValues;
	private RecipeComponentValue<?>[] outputValues;
	private Map<String, RecipeComponentValue<?>> allValueMap;

	public JsonObject originalJson = null;
	private MutableObject<Recipe<?>> originalRecipe = null;
	public JsonObject json = null;
	public boolean changed = false;

	@Override
	public final Scriptable convertJavaToJs(Context cx, Scriptable scope, Class<?> staticType) {
		return new RecipeFunction(cx, scope, staticType, this);
	}

	public void deserialize(boolean merge) {
		for (var v : valueMap.values()) {
			v.key.component.readFromJson(this, UtilsJS.cast(v), json);

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
		for (var v : valueMap.values()) {
			if (v.shouldWrite()) {
				if (v.value == null) {
					throw new RecipeExceptionJS("Value not set for " + v.key + " in recipe " + this);
				}

				v.key.component.writeToJson(this, UtilsJS.cast(v), json);
			}
		}
	}

	public <T> T getValue(RecipeKey<T> key) {
		var v = valueMap.get(key);

		if (v == null) {
			throw new MissingComponentException(key.name, key, valueMap.keySet());
		}

		return UtilsJS.cast(v.value);
	}

	public <T> RecipeJS setValue(RecipeKey<T> key, T value) {
		RecipeComponentValue<T> v = UtilsJS.cast(valueMap.get(key));

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
		for (var recipeKey : valueMap.keySet()) {
			for (var name : recipeKey.names) {
				if (name.equals(key)) {
					return getValue(recipeKey);
				}
			}
		}

		throw new MissingComponentException(key, null, valueMap.keySet());
	}

	// intended for use by scripts
	public RecipeJS set(String key, Object value) {
		for (var recipeKey : valueMap.keySet()) {
			for (var name : recipeKey.names) {
				if (name.equals(key)) {
					setValue(recipeKey, UtilsJS.cast(recipeKey.component.read(this, value)));
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
			valueMap = new IdentityHashMap<>(type.schemaType.schema.keys.length);

			for (int i = 0; i < type.schemaType.schema.keys.length; i++) {
				var v = new RecipeComponentValue<>(type.schemaType.schema.keys[i], i);
				valueMap.put(v.key, v);

				if (created && (v.key.alwaysWrite || !v.key.optional())) {
					if (v.key.alwaysWrite) {
						v.value = UtilsJS.cast(v.key.optional.getDefaultValue(type.schemaType));
					}

					v.write();
				}
			}
		}
	}

	public Map<String, RecipeComponentValue<?>> getAllValueMap() {
		if (allValueMap == null) {
			allValueMap = new HashMap<>();

			for (var v : valueMap.values()) {
				for (var n : v.key.names) {
					allValueMap.put(n, v);
				}
			}
		}

		return allValueMap;
	}

	public void afterLoaded() {
		for (var v : valueMap.values()) {
			var e = v.checkEmpty();

			if (!e.isEmpty()) {
				throw new RecipeExceptionJS(e);
			}
		}
	}

	public final void save() {
		changed = true;
	}

	public RecipeJS id(ResourceLocation _id) {
		id = _id;
		save();
		return this;
	}

	public RecipeJS group(String g) {
		kjs$setGroup(g);
		save();
		return this;
	}

	public RecipeJS merge(JsonObject j) {
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

		for (var vc : valueMap.values()) {
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
	public final RecipeSchema kjs$getSchema() {
		return type.schemaType.schema;
	}

	@Override
	@Deprecated
	public final ResourceLocation kjs$getType() {
		return getType();
	}

	@SuppressWarnings({"SuspiciousToArrayCall", "ToArrayCallWithZeroLengthArrayArgument"})
	public final RecipeComponentValue<?>[] inputValues() {
		if (inputValues == null) {
			if (type.schemaType.schema.inputCount() == 0) {
				inputValues = UtilsJS.cast(RecipeComponentValue.EMPTY_ARRAY);
			} else {
				var list = new ArrayList<>(type.schemaType.schema.inputCount());

				for (var v : valueMap.values()) {
					if (v.key.component.role().isInput()) {
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
				outputValues = UtilsJS.cast(RecipeComponentValue.EMPTY_ARRAY);
			} else {
				var list = new ArrayList<>(type.schemaType.schema.outputCount());

				for (var v : valueMap.values()) {
					if (v.key.component.role().isOutput()) {
						list.add(v);
					}
				}

				outputValues = list.toArray(new RecipeComponentValue[list.size()]);
			}
		}

		return outputValues;
	}

	@Override
	public boolean hasInput(ReplacementMatch match) {
		for (var v : inputValues()) {
			if (v.isInput(this, match)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean replaceInput(ReplacementMatch match, InputReplacement with) {
		boolean replaced = false;

		for (var v : inputValues()) {
			replaced = v.replaceInput(this, match, with) || replaced;
		}

		if (replaced) {
			save();
		}

		return replaced;
	}

	@Override
	public boolean hasOutput(ReplacementMatch match) {
		for (var v : outputValues()) {
			if (v.isOutput(this, match)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean replaceOutput(ReplacementMatch match, OutputReplacement with) {
		boolean replaced = false;

		for (var v : outputValues()) {
			replaced = v.replaceOutput(this, match, with) || replaced;
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
			id = new ResourceLocation(type.id.getNamespace() + ":kjs_" + getUniqueId());
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

	public String getUniqueId() {
		return UtilsJS.getUniqueId(json);
	}

	public RecipeJS stage(String s) {
		json.addProperty("kubejs:stage", s);
		save();
		return this;
	}

	@Nullable
	public Recipe<?> createRecipe() {
		if (removed) {
			return null;
		}

		type.schemaType.getSerializer();

		if (newRecipe || hasChanged()) {
			json.addProperty("type", type.idString);
			serialize();

			if (type.event.stageSerializer != null && json.has("kubejs:stage") && !type.idString.equals("recipestages:stage")) {
				var o = new JsonObject();
				o.addProperty("stage", json.get("kubejs:stage").getAsString());
				o.add("recipe", json);
				return type.event.stageSerializer.fromJson(getOrCreateId(), o);
			}
		} else if (originalRecipe != null) {
			return originalRecipe.getValue();
		}

		return RecipePlatformHelper.get().fromJson(type.schemaType.getSerializer(), getOrCreateId(), json);
	}

	@Nullable
	public Recipe<?> getOriginalRecipe() {
		Throwable error = new Throwable("Original recipe is null!");

		if (originalRecipe == null) {
			originalRecipe = new MutableObject<>();
			try {
				originalRecipe.setValue(RecipePlatformHelper.get().fromJson(type.schemaType.getSerializer(), getOrCreateId(), json));
			} catch (Throwable e) {
				error = e;
			}

			if (originalRecipe == null) {
				if (RecipeJS.itemErrors) {
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

		return getOriginalRecipe().getResultItem();
	}

	public List<Ingredient> getOriginalRecipeIngredients() {
		if (getOriginalRecipe() == null) {
			ConsoleJS.SERVER.warn("Original recipe is null - could not get ingredients");
			return List.of();
		}

		return List.copyOf(getOriginalRecipe().getIngredients());
	}

	public RecipeJS ingredientAction(IngredientActionFilter filter, IngredientAction action) {
		if (json == null) {
			ConsoleJS.SERVER.error("Can't add ingredient action to uninitialized recipe!");
			return this;
		}

		var array = json.get("kubejs:actions") instanceof JsonArray arr ? arr : Util.make(new JsonArray(), (arr) -> json.add("kubejs:actions", arr));
		action.copyFrom(filter);
		array.add(action.toJson());
		save();
		return this;
	}

	public final RecipeJS damageIngredient(IngredientActionFilter filter, int damage) {
		return ingredientAction(filter, new DamageAction(damage));
	}

	public final RecipeJS damageIngredient(IngredientActionFilter filter) {
		return damageIngredient(filter, 1);
	}

	public final RecipeJS replaceIngredient(IngredientActionFilter filter, ItemStack item) {
		return ingredientAction(filter, new ReplaceAction(item));
	}

	public final RecipeJS customIngredientAction(IngredientActionFilter filter, String id) {
		return ingredientAction(filter, new CustomIngredientAction(id));
	}

	public final RecipeJS keepIngredient(IngredientActionFilter filter) {
		return ingredientAction(filter, new KeepAction());
	}

	public final RecipeJS modifyResult(ModifyRecipeResultCallback callback) {
		UUID id = UUID.randomUUID();
		RecipesEventJS.MODIFY_RESULT_CALLBACKS.put(id, callback);
		json.addProperty("kubejs:modify_result", UUIDTypeAdapter.fromUUID(id));
		save();
		return this;
	}

	// Default component serialization methods for ItemComponents and FluidComponents //

	// -- Items -- //

	public boolean inputItemHasPriority(Object from) {
		return from instanceof InputItem || from instanceof ItemStack || from instanceof Ingredient || !InputItem.of(from).isEmpty();
	}

	public InputItem readInputItem(Object from) {
		return InputItem.of(from);
	}

	public JsonElement writeInputItem(InputItem value) {
		return value.ingredient.toJson();
	}

	public boolean outputItemHasPriority(Object from) {
		return from instanceof OutputItem || from instanceof ItemStack || !OutputItem.of(from).isEmpty();
	}

	public OutputItem readOutputItem(Object from) {
		return OutputItem.of(from);
	}

	public JsonElement writeOutputItem(OutputItem value) {
		var json = new JsonObject();
		json.addProperty("item", value.item.kjs$getId());
		json.addProperty("count", value.item.getCount());

		if (value.item.getTag() != null) {
			json.addProperty("nbt", value.item.getTag().toString());
		}

		if (value.hasChance()) {
			json.addProperty("chance", value.getChance());
		}

		return json;
	}

	// -- Fluids -- //

	public boolean inputFluidHasPriority(Object from) {
		return from instanceof InputFluid || from instanceof JsonObject j && j.has("fluid");
	}

	public InputFluid readInputFluid(Object from) {
		return FluidStackJS.of(from);
	}

	public JsonElement writeInputFluid(InputFluid value) {
		return ((FluidStackJS) value).toJson();
	}

	public boolean outputFluidHasPriority(Object from) {
		return from instanceof OutputFluid || from instanceof JsonObject j && j.has("fluid");
	}

	public OutputFluid readOutputFluid(Object from) {
		return FluidStackJS.of(from);
	}

	public JsonElement writeOutputFluid(OutputFluid value) {
		return ((FluidStackJS) value).toJson();
	}

	// -- End -- //
}