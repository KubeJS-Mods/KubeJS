package dev.latvian.mods.kubejs.item;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.core.IngredientSupplierKJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.platform.IngredientPlatformHelper;
import dev.latvian.mods.kubejs.platform.RecipePlatformHelper;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputItem implements IngredientSupplierKJS, InputReplacement {
	public static final InputItem EMPTY = new InputItem(Ingredient.EMPTY, 0);
	public static final Map<String, InputItem> PARSE_CACHE = new HashMap<>();

	public static InputItem of(Ingredient ingredient, int count) {
		return count <= 0 || ingredient == Ingredient.EMPTY ? EMPTY : new InputItem(ingredient, count);
	}

	public static InputItem of(Object o) {
		if (o instanceof InputItem in) {
			return in;
		} else if (o instanceof ItemStack stack) {
			return stack.isEmpty() ? EMPTY : of(Ingredient.of(stack), stack.getCount());
		} else if (o instanceof OutputItem out) {
			return out.isEmpty() ? EMPTY : of(Ingredient.of(out.item), out.getCount());
		} else if (o instanceof CharSequence) {
			var str = o.toString();

			if (str.isEmpty() || str.equals("air")) {
				return EMPTY;
			}

			var cached = PARSE_CACHE.get(str);

			if (cached != null) {
				return cached;
			}

			// parse "Nx ID"

			int x = str.indexOf('x');

			if (x > 0 && x < str.length() - 2 && str.charAt(x + 1) == ' ') {
				try {
					var ingredient = IngredientJS.of(str.substring(x + 2));

					if (ingredient == Ingredient.EMPTY) {
						return EMPTY;
					}

					int count = Integer.parseInt(str.substring(0, x));
					cached = of(IngredientJS.of(str.substring(x + 2)), count);
				} catch (Exception ignore) {
					throw new RecipeExceptionJS("Invalid item input: " + str);
				}
			}

			if (cached == null) {
				cached = of(IngredientJS.of(str), 1);
			}

			PARSE_CACHE.put(str, cached);
			return cached;
		} else if (o instanceof JsonElement json) {
			return ofJson(json);
		}

		return of(IngredientJS.of(o), 1);
	}

	static InputItem ofJson(JsonElement json) {
		if (json == null || json.isJsonNull() || json.isJsonArray() && json.getAsJsonArray().isEmpty()) {
			return EMPTY;
		} else if (json.isJsonPrimitive()) {
			return of(json.getAsString());
		} else if (json.isJsonObject()) {
			var o = json.getAsJsonObject();
			var val = o.has("value");
			var count = o.has("count") ? o.get("count").getAsInt() : 1;

			if (o.has("type")) {
				try {
					return of(RecipePlatformHelper.get().getCustomIngredient(o), count);
				} catch (Exception ex) {
					throw new RecipeExceptionJS("Failed to parse custom ingredient (" + o.get("type") + ") from " + o + ": " + ex);
				}
			} else if (val || o.has("ingredient")) {
				return of(IngredientJS.ofJson(val ? o.get("value") : o.get("ingredient")), count);
			} else if (o.has("tag")) {
				return IngredientPlatformHelper.get().tag(o.get("tag").getAsString()).kjs$withCount(count);
			} else if (o.has("item")) {
				return ItemStackJS.of(o.get("item").getAsString()).getItem().kjs$asIngredient().kjs$withCount(count);
			}

			return EMPTY;
		} else {
			return of(Ingredient.fromJson(json), 1);
		}
	}

	public final Ingredient ingredient;
	public final int count;

	protected InputItem(Ingredient ingredient, int count) {
		this.ingredient = ingredient;
		this.count = count;
	}

	@Override
	public Ingredient kjs$asIngredient() {
		return ingredient;
	}

	public InputItem withCount(int count) {
		return count == this.count ? this : new InputItem(ingredient, count);
	}

	public boolean isEmpty() {
		return this == EMPTY;
	}

	public List<InputItem> unwrap() {
		if (count > 1) {
			var list = new ArrayList<InputItem>(count);
			var single = withCount(1);

			for (int i = 0; i < count; i++) {
				list.add(single);
			}

			return list;
		}

		return List.of(this);
	}

	@Override
	public String toString() {
		if (count > 1) {
			return count + "x " + ingredient;
		}

		return ingredient.toString();
	}

	@Override
	public Object replaceInput(RecipeJS recipe, ReplacementMatch match, InputReplacement original) {
		if (original instanceof InputItem o) {
			return withCount(o.count);
		}

		return this;
	}
}
