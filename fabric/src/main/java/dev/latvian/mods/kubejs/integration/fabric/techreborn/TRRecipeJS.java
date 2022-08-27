package dev.latvian.mods.kubejs.integration.fabric.techreborn;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.ingredient.IngredientStackJS;
import dev.latvian.mods.kubejs.recipe.RecipeArguments;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.component.input.ItemInput;
import dev.latvian.mods.kubejs.recipe.component.input.RecipeItemInputContainer;
import net.minecraft.resources.ResourceLocation;

/**
 * @author LatvianModder
 */
public class TRRecipeJS extends RecipeJS {
	private static class DummyRebornIngredient implements ItemInput {
		public static final ResourceLocation STACK_RECIPE_TYPE = new ResourceLocation("reborncore", "stack");
		public static final ResourceLocation FLUID_RECIPE_TYPE = new ResourceLocation("reborncore", "fluid");
		public static final ResourceLocation TAG_RECIPE_TYPE = new ResourceLocation("reborncore", "tag");
		public static final ResourceLocation WRAPPED_RECIPE_TYPE = new ResourceLocation("reborncore", "wrapped");

		public final ResourceLocation type;
		public final JsonObject json;

		public DummyRebornIngredient(ResourceLocation t, JsonObject o) {
			type = t;
			json = o;
		}

		@Override
		public JsonElement toJson(RecipeItemInputContainer container) {
			return json;
		}
	}

	@Override
	public void create(RecipeArguments args) {
		outputItems.addAll(parseItemOutputList(args.get(0)));
		inputItems.addAll(parseItemInputList(args.get(1)));
		json.addProperty("power", 2);
		json.addProperty("time", 200);

		if (type.toString().equals("techreborn:blast_furnace")) {
			json.addProperty("heat", 1500);
		}
	}

	@Override
	public void deserialize() {
		outputItems.addAll(parseItemOutputList(json.get("results")));
		inputItems.addAll(parseItemInputList(json.get("ingredients")));
	}

	public TRRecipeJS power(int i) {
		json.addProperty("power", i);
		save();
		return this;
	}

	public TRRecipeJS time(int i) {
		json.addProperty("time", i);
		save();
		return this;
	}

	public TRRecipeJS heat(int i) {
		json.addProperty("heat", i);
		save();
		return this;
	}

	@Override
	public void serialize() {
		if (serializeOutputs) {
			var array = new JsonArray();

			for (var out : outputItems) {
				array.add(out.toJson());
			}

			json.add("results", array);
		}

		if (serializeInputs) {
			var array = new JsonArray();

			for (var in : inputItems) {
				array.add(in.toJson());
			}

			json.add("ingredients", array);
		}
	}

	@Override
	public RecipeItemInputContainer parseItemInput(Object o, String key) {
		if (o instanceof JsonObject jsonObj) {

			var type = DummyRebornIngredient.STACK_RECIPE_TYPE;

			if (jsonObj.has("fluid")) {
				type = DummyRebornIngredient.FLUID_RECIPE_TYPE;
			} else if (jsonObj.has("tag")) {
				type = DummyRebornIngredient.TAG_RECIPE_TYPE;
			} else if (jsonObj.has("wrapped")) {
				type = DummyRebornIngredient.WRAPPED_RECIPE_TYPE;
			}

			if (jsonObj.has("type")) {
				type = new ResourceLocation(jsonObj.get("type").getAsString());
			}

			if (!type.equals(DummyRebornIngredient.STACK_RECIPE_TYPE) && !type.equals(DummyRebornIngredient.TAG_RECIPE_TYPE)) {
				RecipeItemInputContainer container = new RecipeItemInputContainer();
				container.recipe = this;
				container.input = new DummyRebornIngredient(type, jsonObj);
				return container;
			}
		}

		return super.parseItemInput(o, key);
	}

	@Override
	public JsonElement serializeIngredientStack(IngredientStackJS in) {
		var o = in.ingredient.toJson().getAsJsonObject();
		o.addProperty("count", in.getCount());
		return o;
	}
}
