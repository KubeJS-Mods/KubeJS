package dev.latvian.mods.kubejs.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.util.UUIDTypeAdapter;
import dev.architectury.platform.Platform;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientStackJS;
import dev.latvian.mods.kubejs.recipe.ingredientaction.CustomIngredientAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.DamageAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientActionFilter;
import dev.latvian.mods.kubejs.recipe.ingredientaction.KeepAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.ReplaceAction;
import dev.latvian.mods.kubejs.recipe.minecraft.CustomRecipeJS;
import dev.latvian.mods.kubejs.server.ServerSettings;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.JsonIO;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.MapJS;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
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
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * @author LatvianModder
 */
public abstract class RecipeJS {
	public static RecipeJS currentRecipe = null;
	public static boolean itemErrors = false;
	private static MessageDigest messageDigest;

	public ResourceLocation id;
	public RecipeTypeJS type;
	public JsonObject originalJson = null;
	public JsonObject json = null;
	public Recipe<?> originalRecipe = null;
	public final List<ItemStackJS> outputItems = new ArrayList<>(1);
	public final List<IngredientJS> inputItems = new ArrayList<>(1);
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

	public RecipeJS merge(Object data) {
		var j = MapJS.json(data);

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

	public final boolean hasInput(IngredientJS ingredient, boolean exact) {
		if (getInputIndex(ingredient, exact) != -1) {
			return true;
		}

		if (originalRecipe != null && this instanceof CustomRecipeJS && ServerSettings.instance.useOriginalRecipeForFilters) {
			try {
				for (var in0 : originalRecipe.getIngredients()) {
					var in = IngredientJS.of(in0);

					if (!in.isEmpty() && (exact ? in.equals(ingredient) : in.anyStackMatches(ingredient))) {
						return true;
					}
				}
			} catch (Exception ignored) {
			}
		}

		return false;
	}

	public final int getInputIndex(IngredientJS ingredient, boolean exact) {
		for (var i = 0; i < inputItems.size(); i++) {
			var in = inputItems.get(i);

			if (exact ? in.equals(ingredient) : in.anyStackMatches(ingredient)) {
				return i;
			}
		}

		return -1;
	}

	public final boolean replaceInput(IngredientJS i, IngredientJS with, boolean exact) {
		return replaceInput(i, with, exact, (in, original) -> in.withCount(original.getCount()));
	}

	public final boolean replaceInput(IngredientJS i, IngredientJS with, boolean exact, BiFunction<IngredientJS, IngredientJS, IngredientJS> func) {
		var changed = false;

		for (var j = 0; j < inputItems.size(); j++) {
			if (exact ? inputItems.get(j).equals(i) : inputItems.get(j).anyStackMatches(i)) {
				inputItems.set(j, convertReplacedInput(j, inputItems.get(j), func.apply(with.copy(), inputItems.get(j))));
				changed = true;
				serializeInputs = true;
				save();
			}
		}

		return changed;
	}

	public final boolean hasOutput(IngredientJS ingredient, boolean exact) {
		if (getOutputIndex(ingredient, exact) != -1) {
			return true;
		}

		if (originalRecipe != null && this instanceof CustomRecipeJS && ServerSettings.instance.useOriginalRecipeForFilters) {
			try {
				var out = ItemStackJS.of(originalRecipe.getResultItem());

				if (!out.isEmpty()) {
					return exact ? ingredient.equals(out) : ingredient.test(out);
				}
			} catch (Exception ignored) {
			}
		}

		return false;
	}

	public final int getOutputIndex(IngredientJS ingredient, boolean exact) {
		for (var i = 0; i < outputItems.size(); i++) {
			var out = outputItems.get(i);

			if (exact ? ingredient.equals(out) : ingredient.test(out)) {
				return i;
			}
		}

		return -1;
	}

	public final boolean replaceOutput(IngredientJS i, ItemStackJS with, boolean exact) {
		return replaceOutput(i, with, exact, (out, original) -> out.withCount(original.getCount()).withChance(original.getChance()));
	}

	public final boolean replaceOutput(IngredientJS i, ItemStackJS with, boolean exact, BiFunction<ItemStackJS, ItemStackJS, ItemStackJS> func) {
		var changed = false;

		for (var j = 0; j < outputItems.size(); j++) {
			if (exact ? i.equals(outputItems.get(j)) : i.test(outputItems.get(j))) {
				outputItems.set(j, convertReplacedOutput(j, outputItems.get(j), func.apply(with.copy(), outputItems.get(j))));
				changed = true;
				serializeOutputs = true;
				save();
			}
		}

		return changed;
	}

	public String getGroup() {
		var e = json.get("group");
		return e instanceof JsonPrimitive ? e.getAsString() : "";
	}

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

	public String getMod() {
		return getOrCreateId().getNamespace();
	}

	public String getPath() {
		return getOrCreateId().getPath();
	}

	public String getType() {
		return type.toString();
	}

	public ResourceLocation getOrCreateId() {
		if (id == null) {
			id = new ResourceLocation(type.getId().getNamespace() + ":kjs_" + getUniqueId());
		}

		return id;
	}

	public IngredientJS convertReplacedInput(int index, IngredientJS oldIngredient, IngredientJS newIngredient) {
		return newIngredient;
	}

	public ItemStackJS convertReplacedOutput(int index, ItemStackJS oldStack, ItemStackJS newStack) {
		return newStack;
	}

	@Nullable
	public ItemStackJS resultFromRecipeJson(JsonObject json) {
		return null;
	}

	@Nullable
	public JsonElement serializeIngredientStack(IngredientStackJS in) {
		return null;
	}

	@Nullable
	public JsonElement serializeItemStack(ItemStackJS stack) {
		return null;
	}

	public IngredientJS parseIngredientItem(@Nullable Object o, String key) {
		var ingredient = IngredientJS.of(o);

		if (ingredient.isInvalidRecipeIngredient() && !Platform.isFabric()) // This is stupid >:(
		{
			if (key.isEmpty()) {
				throw new RecipeExceptionJS(o + " is not a valid ingredient!");
			} else {
				throw new RecipeExceptionJS(o + " with key '" + key + "' is not a valid ingredient!");
			}
		}

		return ingredient;
	}

	public IngredientJS parseIngredientItem(@Nullable Object o) {
		return parseIngredientItem(o, "");
	}

	public ItemStackJS parseResultItem(@Nullable Object o) {
		var result = ItemStackJS.of(o);

		if (result.isInvalidRecipeIngredient() && !Platform.isFabric()) // This is stupid >:(
		{
			throw new RecipeExceptionJS(o + " is not a valid result!");
		}

		return result;
	}

	public List<IngredientJS> parseIngredientItemList(@Nullable Object o) {
		List<IngredientJS> list = new ArrayList<>();

		if (o instanceof JsonElement elem) {
			var array = elem instanceof JsonArray arr ? arr : Util.make(new JsonArray(), (arr) -> arr.add(elem));
			for (var e : array) {
				list.add(parseIngredientItem(e));
			}
		} else {
			for (var o1 : ListJS.orSelf(o)) {
				list.add(parseIngredientItem(o1));
			}
		}

		return list;
	}

	public List<IngredientStackJS> parseIngredientItemStackList(@Nullable Object o) {
		return parseIngredientItemList(o).stream().map(IngredientJS::asIngredientStack).collect(Collectors.toList());
	}

	public List<ItemStackJS> parseResultItemList(@Nullable Object o) {
		List<ItemStackJS> list = new ArrayList<>();

		if (o instanceof JsonElement elem) {
			var array = elem instanceof JsonArray arr ? arr : Util.make(new JsonArray(), (arr) -> arr.add(elem));
			for (var e : array) {
				list.add(parseResultItem(e));
			}
		} else {
			for (var o1 : ListJS.orSelf(o)) {
				list.add(parseResultItem(o1));
			}
		}

		return list;
	}

	public String getFromToString() {
		return inputItems + " -> " + outputItems;
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

	public ItemStackJS getOriginalRecipeResult() {
		if (originalRecipe == null) {
			ConsoleJS.SERVER.warn("Original recipe is null - could not get result");
			return ItemStackJS.EMPTY;
		}

		return ItemStackJS.of(originalRecipe.getResultItem());
	}

	public List<IngredientJS> getOriginalRecipeIngredients() {
		if (originalRecipe == null) {
			ConsoleJS.SERVER.warn("Original recipe is null - could not get ingredients");
			return new ArrayList<>();
		}

		return originalRecipe.getIngredients().stream().map(IngredientJS::of).collect(Collectors.toList());
	}

	/**
	 * Only used when a recipe has sub-recipes, e.g. create:sequenced_assembly
	 */
	public void dontAdd() {
		RecipeEventJS.instance.addedRecipes.remove(this);
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

	public final RecipeJS replaceIngredient(IngredientActionFilter filter, ItemStackJS item) {
		return ingredientAction(filter, new ReplaceAction(item.getItemStack()));
	}

	public final RecipeJS customIngredientAction(IngredientActionFilter filter, String id) {
		return ingredientAction(filter, new CustomIngredientAction(id));
	}

	public final RecipeJS keepIngredient(IngredientActionFilter filter) {
		return ingredientAction(filter, new KeepAction());
	}

	public final RecipeJS modifyResult(ModifyRecipeResultCallback callback) {
		UUID id = UUID.randomUUID();
		RecipeEventJS.modifyResultCallbackMap.put(id, callback);
		json.addProperty("kubejs_modify_result", UUIDTypeAdapter.fromUUID(id));
		save();
		return this;
	}
}