package dev.latvian.mods.kubejs.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.util.UUIDTypeAdapter;
import dev.architectury.platform.Platform;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.core.RecipeKJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientStack;
import dev.latvian.mods.kubejs.platform.RecipePlatformHelper;
import dev.latvian.mods.kubejs.recipe.ingredientaction.CustomIngredientAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.DamageAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientActionFilter;
import dev.latvian.mods.kubejs.recipe.ingredientaction.KeepAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.ReplaceAction;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.JsonIO;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public abstract class RecipeJS implements RecipeKJS {
	public static RecipeJS currentRecipe = null;
	public static boolean itemErrors = false;
	private static MessageDigest messageDigest;

	public ResourceLocation id;
	public RecipeTypeJS type;
	public JsonObject originalJson = null;
	public JsonObject json = null;
	public Recipe<?> originalRecipe = null;
	public boolean serializeOutputs;
	public boolean serializeInputs;
	private String recipeStage = "";

	public abstract void create(RecipeArguments args);

	public abstract void deserialize();

	public abstract void serialize();

	public final void deserializeJson() {
		currentRecipe = this;
		deserialize();

		if (CommonProperties.get().debugInfo) {
			originalJson = (JsonObject) JsonIO.copy(json);
		}

		currentRecipe = null;
	}

	public final void serializeJson() {
		currentRecipe = this;
		json.addProperty("type", type.getId().toString());
		serialize();
		currentRecipe = null;
	}

	public final void save() {
		originalRecipe = null;
	}

	public RecipeJS merge(JsonObject j) {
		if (j != null) {
			for (var entry : j.entrySet()) {
				json.add(entry.getKey(), entry.getValue());
			}

			save();
		}

		return this;
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
	public final boolean kjs$replaceInput(IngredientMatch match, Ingredient with, ItemInputTransformer transformer) {
		return replaceInput(match, with, transformer);
	}

	@Override
	@Deprecated
	public final boolean kjs$hasOutput(IngredientMatch match) {
		return hasOutput(match);
	}

	@Override
	@Deprecated
	public final boolean kjs$replaceOutput(IngredientMatch match, ItemStack with, ItemOutputTransformer transformer) {
		return replaceOutput(match, with, transformer);
	}

	// RecipeKJS methods //

	@HideFromJS
	public abstract boolean hasInput(IngredientMatch match);

	@HideFromJS
	public abstract boolean replaceInput(IngredientMatch match, Ingredient with, ItemInputTransformer transformer);

	@HideFromJS
	public abstract boolean hasOutput(IngredientMatch match);

	@HideFromJS
	public abstract boolean replaceOutput(IngredientMatch match, ItemStack with, ItemOutputTransformer transformer);

	/*

	@Override
	public final boolean hasInput(Ingredient match, boolean exact) {
		return getInputIndex(match, exact) != -1;
	}

	public final int getInputIndex(Ingredient match, boolean exact) {
		for (var i = 0; i < inputItems.size(); i++) {
			var in = inputItems.get(i);

			if (exact ? in.input.equals(match) : in.input.contains(match)) {
				return i;
			}
		}

		return -1;
	}

	public final boolean replaceInput(Ingredient match, Ingredient with, boolean exact) {
		return replaceInput(match, with, exact, ItemInputTransformer.DEFAULT);
	}

	public final boolean replaceInput(Ingredient match, Ingredient with, boolean exact, ItemInputTransformer transformer) {
		var changed = false;

		for (var j = 0; j < inputItems.size(); j++) {
			var in = inputItems.get(j);

			if (exact ? in.input.equals(match) : in.input.contains(match)) {
				in.input = transformReplacedInput(j, in.input, transformer.transform(this, with, in.input));
				changed = true;
				serializeInputs = true;
				save();
			}
		}

		return changed;
	}

	@Override
	public final boolean hasOutput(Ingredient match, boolean exact) {
		return getOutputIndex(match, exact) != -1;
	}

	public final int getOutputIndex(Ingredient match, boolean exact) {
		for (var i = 0; i < outputItems.size(); i++) {
			var out = outputItems.get(i);

			if (exact ? match.equalsOutput(out.output) : match.containsOutput(out.output)) {
				return i;
			}
		}

		return -1;
	}

	public final boolean replaceOutput(Ingredient match, ItemStack with, boolean exact) {
		return replaceOutput(match, with, exact, ItemOutputTransformer.DEFAULT);
	}

	public final boolean replaceOutput(Ingredient match, ItemStack with, boolean exact, ItemOutputTransformer transformer) {
		var changed = false;

		for (var j = 0; j < outputItems.size(); j++) {
			var out = outputItems.get(j);

			if (exact ? match.equalsOutput(out.output) : match.containsOutput(out.output)) {
				out.output = transformReplacedOutput(j, out.output, transformer.transform(this, with, out.output));
				changed = true;
				serializeOutputs = true;
				save();
			}
		}

		return changed;
	}

	*/

	@HideFromJS
	public String getGroup() {
		var e = json.get("group");
		return e instanceof JsonPrimitive ? e.getAsString() : "";
	}

	@HideFromJS
	public void setGroup(String g) {
		if (g.isEmpty()) {
			json.remove("group");
		} else {
			json.addProperty("group", g);
		}

		save();
	}

	@Override
	public String toString() {
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
		return type.getId();
	}

	@HideFromJS
	public ResourceLocation getOrCreateId() {
		if (id == null) {
			id = new ResourceLocation(type.getId().getNamespace() + ":kjs_" + getUniqueId());
		}

		return id;
	}

	@Nullable
	public ItemStack resultFromRecipeJson(JsonObject json) {
		return null;
	}

	@Nullable
	public JsonElement serializeIngredientStack(IngredientStack in) {
		return null;
	}

	@Nullable
	public JsonElement serializeItemStack(ItemStack stack) {
		return null;
	}

	public Ingredient parseItemInput(@Nullable Object o, String key) {
		var ingredient = IngredientJS.of(o);

		if (ingredient.kjs$isInvalidRecipeIngredient()) {
			if (key.isEmpty()) {
				throw new RecipeExceptionJS(o + " is not a valid ingredient!");
			} else {
				throw new RecipeExceptionJS(o + " with key '" + key + "' is not a valid ingredient!");
			}
		}

		return ingredient;
	}

	public Ingredient parseItemInput(@Nullable Object o) {
		return parseItemInput(o, "");
	}

	public ItemStack parseItemOutput(@Nullable Object o) {
		var result = ItemStackJS.of(o);

		if (result == null || result.isEmpty()) {
			throw new RecipeExceptionJS(o + " is not a valid result!");
		}

		return result;
	}

	public List<Ingredient> parseItemInputList(@Nullable Object o) {
		List<Ingredient> list = new ArrayList<>();

		if (o instanceof JsonElement elem) {
			var array = elem instanceof JsonArray arr ? arr : Util.make(new JsonArray(), (arr) -> arr.add(elem));
			for (var e : array) {
				list.add(parseItemInput(e));
			}
		} else {
			for (var o1 : ListJS.orSelf(o)) {
				list.add(parseItemInput(o1));
			}
		}

		return list;
	}

	public List<ItemStack> parseItemOutputList(@Nullable Object o) {
		List<ItemStack> list = new ArrayList<>();

		if (o instanceof JsonElement elem) {
			var array = elem instanceof JsonArray arr ? arr : Util.make(new JsonArray(), (arr) -> arr.add(elem));
			for (var e : array) {
				list.add(parseItemOutput(e));
			}
		} else {
			for (var o1 : ListJS.orSelf(o)) {
				list.add(parseItemOutput(o1));
			}
		}

		return list;
	}

	public String getFromToString() {
		return "unknown -> unknown";
	}

	public String getUniqueId() {
		if (messageDigest == null) {
			try {
				messageDigest = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException nsae) {
				throw new InternalError("MD5 not supported", nsae);
			}
		}

		if (messageDigest == null) {
			return new BigInteger(HexFormat.of().formatHex(JsonIO.getJsonHashBytes(json)), 16).toString(36);
		} else {
			messageDigest.reset();
			return new BigInteger(HexFormat.of().formatHex(messageDigest.digest(JsonIO.getJsonHashBytes(json))), 16).toString(36);
		}
	}

	public RecipeJS stage(String s) {
		recipeStage = s;
		save();

		if (!Platform.isModLoaded("recipestages")) {
			ConsoleJS.SERVER.warn("Recipe requires stage '" + recipeStage + "' but Recipe Stages mod isn't installed!");
		}

		return this;
	}

	public Recipe<?> createRecipe() throws Throwable {
		serializeJson();

		if (!recipeStage.isEmpty()) {
			if (Platform.isModLoaded("recipestages")) {
				var stageSerializer = KubeJSRegistries.recipeSerializers().get(new ResourceLocation("recipestages:stage"));
				var o = new JsonObject();
				o.addProperty("stage", recipeStage);
				o.add("recipe", json);
				return stageSerializer.fromJson(getOrCreateId(), o);
			}
		}

		return Objects.requireNonNull(RecipePlatformHelper.get().fromJson(this));
	}

	public ItemStack getOriginalRecipeResult() {
		if (originalRecipe == null) {
			ConsoleJS.SERVER.warn("Original recipe is null - could not get result");
			return ItemStack.EMPTY;
		}

		return originalRecipe.getResultItem();
	}

	public List<Ingredient> getOriginalRecipeIngredients() {
		if (originalRecipe == null) {
			ConsoleJS.SERVER.warn("Original recipe is null - could not get ingredients");
			return List.of();
		}

		return List.copyOf(originalRecipe.getIngredients());
	}

	/**
	 * Only used when a recipe has sub-recipes, e.g. create:sequenced_assembly
	 */
	public void dontAdd() {
		RecipesEventJS.instance.addedRecipes.remove(this);
	}

	public boolean serializeNBTAsJson() {
		return type != null && type.getMod().equals("techreborn");
	}

	public RecipeJS ingredientAction(IngredientActionFilter filter, IngredientAction action) {
		var array = json.get("kubejs_actions") instanceof JsonArray arr ? arr : Util.make(new JsonArray(), (arr) -> json.add("kubejs_actions", arr));
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
		json.addProperty("kubejs_modify_result", UUIDTypeAdapter.fromUUID(id));
		save();
		return this;
	}

	public JsonElement itemToJson(ItemStack stack) {
		return stack.kjs$toJson();
	}
}