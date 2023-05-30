package dev.latvian.mods.kubejs.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.util.UUIDTypeAdapter;
import dev.latvian.mods.kubejs.core.RecipeKJS;
import dev.latvian.mods.kubejs.item.EmptyItemError;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.platform.RecipePlatformHelper;
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
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RecipeJS implements RecipeKJS, CustomJavaToJsWrapper {
	public static boolean itemErrors = false;

	public ResourceLocation id;
	public RecipeTypeFunction type;
	public boolean newRecipe;
	public boolean removed;
	private RecipeComponentValue<?>[] values = RecipeComponentValue.EMPTY_ARRAY;
	private Map<String, RecipeComponentValue<?>> allValueMap;

	public JsonObject originalJson = null;
	private Recipe<?> originalRecipe = null;
	public JsonObject json = null;
	public boolean changed = false;

	@Override
	public final Scriptable convertJavaToJs(Context cx, Scriptable scope, Class<?> staticType) {
		return new RecipeFunction(cx, scope, staticType, this);
	}

	public void deserialize() {
		for (var v : values) {
			v.key.component().readJson(UtilsJS.cast(v), json);
		}
	}

	public void serialize() {
		for (var v : values) {
			if (v.changed) {
				v.key.component().writeJson(UtilsJS.cast(v), json);
			}
		}
	}

	public boolean hasChanged(RecipeKey<?> key) {
		return changed && values[key.index()].changed;
	}

	public <T> T getValue(RecipeKey<T> key) {
		var v = values[key.index()].value;

		if (v == null) {
			return key.component().optionalValue();
		}

		return UtilsJS.cast(v);
	}

	public RecipeJS setValue(RecipeKey<?> key, Object value) {
		if (key.index() < 0 || key.index() >= values.length) {
			throw new RecipeExceptionJS("Tried to set key '" + key.name() + "' with index " + key.index() + " for type '" + type + "' but only " + values.length + " values available!");
		}

		values[key.index()].value = UtilsJS.cast(value);
		values[key.index()].changed = true;
		changed = true;
		return this;
	}

	public RecipeJS set(String key, Object value) {
		for (var k : type.schemaType.schema.keys) {
			if (k.name().equals(key)) {
				return setValue(k, value);
			}
		}

		throw new RecipeExceptionJS("Key '" + key + "' of type '" + type + "' not found!");
	}

	public void initValues(RecipeSchema schema) {
		if (schema.keys.length > 0) {
			values = new RecipeComponentValue[schema.keys.length];

			for (int i = 0; i < schema.keys.length; i++) {
				values[i] = new RecipeComponentValue<>(this, schema.keys[i]);
			}
		}
	}

	public void setAllChanged(boolean changed) {
		this.changed = changed;

		for (var v : values) {
			v.changed = changed;
		}
	}

	public Map<String, RecipeComponentValue<?>> getAllValueMap() {
		if (allValueMap == null) {
			allValueMap = new HashMap<>();

			for (var v : values) {
				for (var n : v.key.altNames()) {
					allValueMap.put(n, v);
				}
			}

			for (var v : values) {
				allValueMap.put(v.key.name(), v);
			}
		}

		return allValueMap;
	}

	public void afterLoaded() {
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
		throw new RecipeExceptionJS("This recipe type has integration, so merge() isn't supported!");
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

	@Override
	public boolean hasInput(ReplacementMatch match) {
		for (var key : type.schemaType.schema.inputKeys) {
			if (values[key].isInput(match)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean replaceInput(ReplacementMatch match, InputReplacement with) {
		boolean replaced = false;

		for (var key : type.schemaType.schema.inputKeys) {
			replaced = values[key].replaceInput(match, with) || replaced;
		}

		changed |= replaced;
		return replaced;
	}


	@Override
	public boolean hasOutput(ReplacementMatch match) {
		for (var key : type.schemaType.schema.outputKeys) {
			if (values[key].isOutput(match)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean replaceOutput(ReplacementMatch match, OutputReplacement with) {
		boolean replaced = false;

		for (var key : type.schemaType.schema.outputKeys) {
			replaced = values[key].replaceOutput(match, with) || replaced;
		}

		changed |= replaced;
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

		for (var key : type.schemaType.schema.inputKeys) {
			if (sb.length() > 1) {
				sb.append(",");
			}

			sb.append(values[key]);
		}

		sb.append("] -> [");

		for (var key : type.schemaType.schema.outputKeys) {
			if (sb.length() > 1) {
				sb.append(",");
			}

			sb.append(values[key]);
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

		if (changed || newRecipe) {
			json.addProperty("type", type.idString);
			serialize();

			if (type.event.stageSerializer != null && json.has("kubejs:stage") && !type.idString.equals("recipestages:stage")) {
				var o = new JsonObject();
				o.addProperty("stage", json.get("kubejs:stage").getAsString());
				o.add("recipe", json);
				return type.event.stageSerializer.fromJson(getOrCreateId(), o);
			}
		} else if (originalRecipe != null) {
			return originalRecipe;
		}

		return RecipePlatformHelper.get().fromJson(type.schemaType.getSerializer(), getOrCreateId(), json);
	}

	public Recipe<?> getOriginalRecipe() {
		if (originalRecipe == null) {
			originalRecipe = id == null ? null : RecipePlatformHelper.get().fromJson(type.schemaType.getSerializer(), id, json);

			if (originalRecipe == null) {
				throw new RecipeExceptionJS("Could not create recipe from json for " + this);
			}
		}

		return originalRecipe;
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

	/**
	 * Only used when a recipe has sub-recipes, e.g. create:sequenced_assembly
	 */
	public boolean shouldAdd() {
		return true;
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
		RecipesEventJS.modifyResultCallbackMap.put(id, callback);
		json.addProperty("kubejs:modify_result", UUIDTypeAdapter.fromUUID(id));
		save();
		return this;
	}

	// Default component serialization methods for ItemComponents and FluidComponents //

	public boolean shouldReadInputItem(Object from) {
		return !InputItem.of(from).isEmpty();
	}

	public InputItem readInputItem(Object from) {
		var i = InputItem.of(from);

		if (i.isEmpty()) {
			throw new EmptyItemError(from + " is not a valid ingredient!", from);
		}

		return i;
	}

	public JsonElement writeInputItem(InputItem value) {
		return value == InputItem.EMPTY ? null : value.ingredient.toJson();
	}

	public boolean shouldReadOutputItem(Object from) {
		return !OutputItem.of(from).isEmpty();
	}

	public OutputItem readOutputItem(Object from) {
		var i = OutputItem.of(from);

		if (i.isEmpty()) {
			throw new EmptyItemError(from + " is not a valid result!", from);
		}

		return i;
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
}