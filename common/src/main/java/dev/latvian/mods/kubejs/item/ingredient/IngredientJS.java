package dev.latvian.mods.kubejs.item.ingredient;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipePlatformHelper;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.kubejs.util.WrappedJS;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.mod.util.Copyable;
import dev.latvian.mods.rhino.mod.util.JsonSerializable;
import dev.latvian.mods.rhino.mod.util.NBTUtils;
import dev.latvian.mods.rhino.regexp.NativeRegExp;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author LatvianModder
 */
public interface IngredientJS extends JsonSerializable, WrappedJS, Copyable {
	static Ingredient of(@Nullable Object o) {
		while (o instanceof Wrapper w) {
			o = w.unwrap();
		}

		if (o == null || o == ItemStack.EMPTY || o == Items.AIR) {
			return Ingredient.EMPTY;
		} else if (o instanceof Ingredient ingr) {
			return ingr;
		} else if (o instanceof Pattern || o instanceof NativeRegExp) {
			var reg = UtilsJS.parseRegex(o);

			if (reg != null) {
				return new RegExIngredient(reg);
			}

			return Ingredient.EMPTY;
		} else if (o instanceof JsonElement json) {
			return ingredientFromRecipeJson(json);
		} else if (o instanceof CharSequence) {
			var s = o.toString();
			var count = 1;
			var spaceIndex = s.indexOf(' ');

			if (spaceIndex >= 2 && s.indexOf('x') == spaceIndex - 1) {
				count = Integer.parseInt(s.substring(0, spaceIndex - 1));
				s = s.substring(spaceIndex + 1);
			}

			if (RecipeJS.itemErrors && count <= 0) {
				throw new RecipeExceptionJS("Invalid count!").error();
			}

			if (s.equals("*")) {
				return WildcardIngredient.INSTANCE;
			} else if (s.isEmpty() || s.equals("-") || s.equals("air") || s.equals("minecraft:air")) {
				return Ingredient.EMPTY;
			} else if (s.startsWith("#")) {
				return TagIngredient.ofTag(s.substring(1));
			} else if (s.startsWith("@")) {
				return ModIngredient.ofMod(s.substring(1));
			} else if (s.startsWith("%")) {
				var group = ItemStackJS.findCreativeTab(s.substring(1));

				if (group == null) {
					if (RecipeJS.itemErrors) {
						throw new RecipeExceptionJS("Item group '" + s.substring(1) + "' not found!").error();
					}

					return Ingredient.EMPTY;
				}

				return new CreativeTabIngredient(group);
			}

			var reg = UtilsJS.parseRegex(s);

			if (reg != null) {
				return new RegExIngredient(reg);
			}

			var item = KubeJSRegistries.items().get(new ResourceLocation(s));

			if (item == null || item == Items.AIR) {
				return Ingredient.EMPTY;
			}

			return item.kjs$getTypeIngredient();
		}

		List<?> list = ListJS.of(o);

		if (list != null) {
			var inList = new ArrayList<Ingredient>(list.size());

			for (var o1 : list) {
				var ingredient = of(o1);

				if (ingredient != Ingredient.EMPTY) {
					inList.add(ingredient);
				}
			}

			return OrIngredient.ofList(inList);
		}

		var map = MapJS.of(o);

		if (map != null) {
			Ingredient in = Ingredient.EMPTY;
			var val = map.containsKey("value");

			if (map.containsKey("type")) {
				if ("forge:nbt".equals(map.get("type"))) {
					in = ItemStackJS.of(map.get("item")).kjs$withNBT(NBTUtils.toTagCompound(map.get("nbt"))).kjs$asIngredient();
				} else {
					var json = MapJS.json(o);

					if (json == null) {
						throw new RecipeExceptionJS("Failed to parse custom ingredient (" + o + " is not a json object").fallback();
					}

					try {
						in = RecipePlatformHelper.get().getCustomIngredient(json);
					} catch (Exception ex) {
						throw new RecipeExceptionJS("Failed to parse custom ingredient (" + json.get("type") + ") from " + json + ": " + ex).fallback();
					}
				}
			} else if (val || map.containsKey("ingredient")) {
				in = of(val ? map.get("value") : map.get("ingredient"));
			} else if (map.containsKey("tag")) {
				in = TagIngredient.ofTag(map.get("tag").toString());
			} else if (map.containsKey("item")) {
				in = ItemStackJS.of(map).getItem().kjs$getTypeIngredient();
			}

			return in;
		}

		return ItemStackJS.of(o).kjs$asIngredient();
	}

	static Ingredient ingredientFromRecipeJson(JsonElement json) {
		if (json.isJsonArray()) {
			var inList = new ArrayList<Ingredient>();

			for (var e : json.getAsJsonArray()) {
				var i = ingredientFromRecipeJson(e);

				if (i != Ingredient.EMPTY) {
					inList.add(i);
				}
			}

			return OrIngredient.ofList(inList);

		} else if (json.isJsonPrimitive()) {
			return of(json.getAsString());
		} else if (json.isJsonObject()) {
			var o = json.getAsJsonObject();
			Ingredient in = Ingredient.EMPTY;
			var val = o.has("value");

			if (o.has("type")) {
				try {
					in = RecipePlatformHelper.get().getCustomIngredient(o);
				} catch (Exception ex) {
					throw new RecipeExceptionJS("Failed to parse custom ingredient (" + o.get("type") + ") from " + o + ": " + ex);
				}
			} else if (val || o.has("ingredient")) {
				in = ingredientFromRecipeJson(val ? o.get("value") : o.get("ingredient"));
			} else if (o.has("tag")) {
				in = TagIngredient.ofTag(o.get("tag").getAsString());
			} else if (o.has("item")) {
				in = ItemStackJS.of(o.get("item").getAsString()).getItem().kjs$getTypeIngredient();
			}

			return in;
		}

		return Ingredient.EMPTY;
	}
}