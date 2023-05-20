package dev.latvian.mods.kubejs.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.util.UUIDTypeAdapter;
import dev.latvian.mods.kubejs.core.RecipeKJS;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.platform.RecipePlatformHelper;
import dev.latvian.mods.kubejs.recipe.component.OptionalRecipeComponent;
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
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class RecipeJS implements RecipeKJS {
	public static boolean itemErrors = false;

	public ResourceLocation id;
	public RecipeFunction type;
	public boolean newRecipe;
	public boolean removed;
	public boolean modified;
	protected RecipeComponentValue<?>[] values = RecipeComponentValue.EMPTY_ARRAY;

	public JsonObject originalJson = null;
	private Recipe<?> originalRecipe = null;
	public JsonObject json = null;
	public boolean changed = false;

	public void deserialize() {
		for (var k : type.schemaType.schema.keys) {
			var j = json.get(k.name());

			if (j == null && k.component() instanceof OptionalRecipeComponent) {
				continue;
			}

			setValue(k, k.component().read(j));
		}
	}

	public void serialize() {
		for (var k : type.schemaType.schema.keys) {
			if (hasChanged(k)) {
				json.add(k.name(), k.component().write(UtilsJS.cast(getValue(k))));
			}
		}
	}

	public boolean hasChanged(RecipeKey<?> key) {
		return changed && values[key.index()].changed;
	}

	public <T> T getValue(RecipeKey<T> key) {
		var v = values[key.index()].value;

		if (v == null && key.component() instanceof OptionalRecipeComponent<T> optional) {
			return optional.defaultValue();
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

	public void initValues(RecipeSchema schema, boolean changed) {
		this.changed = changed;

		if (schema.keys.length > 0) {
			values = new RecipeComponentValue[schema.keys.length];

			for (int i = 0; i < schema.keys.length; i++) {
				values[i] = new RecipeComponentValue<>(schema.keys[i]);
				values[i].changed = changed;
			}
		}
	}

	public void afterLoaded(boolean created) {
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
		setGroup(g);
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
		return getGroup();
	}

	@Override
	@Deprecated
	public final void kjs$setGroup(String group) {
		setGroup(group);
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
	@Deprecated
	public final boolean kjs$hasInput(IngredientMatch match) {
		return hasInput(match);
	}

	@Override
	@Deprecated
	public final boolean kjs$replaceInput(IngredientMatch match, InputItem with, InputItemTransformer transformer) {
		return replaceInput(match, with, transformer);
	}

	@Override
	@Deprecated
	public final boolean kjs$hasOutput(IngredientMatch match) {
		return hasOutput(match);
	}

	@Override
	@Deprecated
	public final boolean kjs$replaceOutput(IngredientMatch match, OutputItem with, OutputItemTransformer transformer) {
		return replaceOutput(match, with, transformer);
	}

	// RecipeKJS methods //

	@HideFromJS
	public boolean hasInput(IngredientMatch match) {
		for (var key : type.schemaType.schema.inputKeys) {
			if (values[key].hasInput(match)) {
				return true;
			}
		}

		return false;
	}

	@HideFromJS
	public boolean replaceInput(IngredientMatch match, InputItem with, InputItemTransformer transformer) {
		boolean replaced = false;

		for (var key : type.schemaType.schema.inputKeys) {
			replaced = values[key].replaceInput(match, with, transformer) || replaced;
		}

		return replaced;
	}


	@HideFromJS
	public boolean hasOutput(IngredientMatch match) {
		for (var key : type.schemaType.schema.outputKeys) {
			if (values[key].hasOutput(match)) {
				return true;
			}
		}

		return false;
	}

	@HideFromJS
	public boolean replaceOutput(IngredientMatch match, OutputItem with, OutputItemTransformer transformer) {
		boolean replaced = false;

		for (var key : type.schemaType.schema.outputKeys) {
			replaced = values[key].replaceOutput(match, with, transformer) || replaced;
		}

		return replaced;
	}

	@HideFromJS
	public String getGroup() {
		var e = json.get("group");
		return e instanceof JsonPrimitive ? e.getAsString() : "";
	}

	@HideFromJS
	public void setGroup(String g) {
		if (!getGroup().equals(g)) {
			if (g.isEmpty()) {
				json.remove("group");
			} else {
				json.addProperty("group", g);
			}

			save();
		}
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
		return "unknown -> unknown";
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

		if (changed) {
			json.addProperty("type", type.idString);
			serialize();

			if (type.event.stageSerializer != null && json.has("kubejs:stage") && !type.idString.equals("recipestages:stage")) {
				var o = new JsonObject();
				o.addProperty("stage", json.get("kubejs:stage").getAsString());
				o.add("recipe", json);
				return type.event.stageSerializer.fromJson(getOrCreateId(), o);
			}
		}

		if (!newRecipe && originalRecipe != null) {
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
}