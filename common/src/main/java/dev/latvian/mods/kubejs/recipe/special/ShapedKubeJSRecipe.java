package dev.latvian.mods.kubejs.recipe.special;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import dev.architectury.core.AbstractRecipeSerializer;
import dev.latvian.mods.kubejs.recipe.KubeJSRecipeEventHandler;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientAction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ShapedKubeJSRecipe extends ShapedRecipe {
	private String group;
	private int width;
	private int height;
	private NonNullList<Ingredient> ingredients;
	private ItemStack result;
	private boolean mirror;
	private boolean shrink;
	private List<IngredientAction> ingredientActions;

	public ShapedKubeJSRecipe(ResourceLocation _id) {
		super(_id, "", 0, 0, NonNullList.withSize(0, Ingredient.EMPTY), ItemStack.EMPTY);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return KubeJSRecipeEventHandler.SHAPED.get();
	}

	@Override
	public String getGroup() {
		return group;
	}

	@Override
	public ItemStack getResultItem() {
		return result;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return ingredients;
	}

	@Override
	public boolean canCraftInDimensions(int w, int h) {
		return w >= width && h >= height;
	}

	@Override
	public boolean matches(CraftingContainer craftingContainer, Level level) {
		for (int x = 0; x <= craftingContainer.getWidth() - width; x++) {
			for (int y = 0; y <= craftingContainer.getHeight() - height; y++) {
				if (mirror && matches(craftingContainer, x, y, true)) {
					return true;
				}

				if (matches(craftingContainer, x, y, false)) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean matches(CraftingContainer craftingContainer, int x0, int y0, boolean mirrorPattern) {
		for (int x = 0; x < craftingContainer.getWidth(); x++) {
			for (int y = 0; y < craftingContainer.getHeight(); y++) {
				int m = x - x0;
				int n = y - y0;
				Ingredient ingredient = Ingredient.EMPTY;

				if (m >= 0 && n >= 0 && m < width && n < height) {
					if (mirrorPattern) {
						ingredient = ingredients.get(width - m - 1 + n * width);
					} else {
						ingredient = ingredients.get(m + n * width);
					}
				}

				if (!ingredient.test(craftingContainer.getItem(x + y * craftingContainer.getWidth()))) {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public ItemStack assemble(CraftingContainer craftingContainer) {
		return getResultItem().copy();
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
		NonNullList<ItemStack> list = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);

		for (int i = 0; i < list.size(); i++) {
			list.set(i, IngredientAction.getRemaining(container, i, ingredientActions));
		}

		return list;
	}

	private static NonNullList<Ingredient> dissolvePattern(String[] pattern, Map<String, Ingredient> key, int w, int h) {
		NonNullList<Ingredient> nonNullList = NonNullList.withSize(w * h, Ingredient.EMPTY);
		Set<String> set = Sets.newHashSet(key.keySet());
		set.remove(" ");

		for (int k = 0; k < pattern.length; ++k) {
			for (int l = 0; l < pattern[k].length(); ++l) {
				String string = pattern[k].substring(l, l + 1);
				Ingredient ingredient = key.get(string);
				if (ingredient == null) {
					throw new JsonSyntaxException("Pattern references symbol '" + string + "' but it's not defined in the key");
				}

				set.remove(string);
				nonNullList.set(l + w * k, ingredient);
			}
		}

		if (!set.isEmpty()) {
			throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
		} else {
			return nonNullList;
		}
	}

	private static String[] shrink(String[] strings) {
		int i = Integer.MAX_VALUE;
		int j = 0;
		int k = 0;
		int l = 0;

		for (int m = 0; m < strings.length; ++m) {
			String string = strings[m];
			i = Math.min(i, firstNonSpace(string));
			int n = lastNonSpace(string);
			j = Math.max(j, n);
			if (n < 0) {
				if (k == m) {
					++k;
				}

				++l;
			} else {
				l = 0;
			}
		}

		if (strings.length == l) {
			return new String[0];
		} else {
			String[] strings2 = new String[strings.length - l - k];

			for (int o = 0; o < strings2.length; ++o) {
				strings2[o] = strings[o + k].substring(i, j + 1);
			}

			return strings2;
		}
	}

	private static int firstNonSpace(String string) {
		int i;
		for (i = 0; i < string.length() && string.charAt(i) == ' '; ++i) {
		}

		return i;
	}

	private static int lastNonSpace(String string) {
		int i;
		for (i = string.length() - 1; i >= 0 && string.charAt(i) == ' '; --i) {
		}

		return i;
	}

	private static String[] patternFromJson(JsonArray jsonArray) {
		String[] strings = new String[jsonArray.size()];
		if (strings.length > 3) {
			throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");
		} else if (strings.length == 0) {
			throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
		} else {
			for (int i = 0; i < strings.length; ++i) {
				String string = GsonHelper.convertToString(jsonArray.get(i), "pattern[" + i + "]");
				if (string.length() > 3) {
					throw new JsonSyntaxException("Invalid pattern: too many columns, 3 is maximum");
				}

				if (i > 0 && strings[0].length() != string.length()) {
					throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
				}

				strings[i] = string;
			}

			return strings;
		}
	}

	private static Map<String, Ingredient> keyFromJson(JsonObject jsonObject) {
		Map<String, Ingredient> map = Maps.newHashMap();

		for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			if (entry.getKey().length() != 1) {
				throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only).");
			}

			if (" ".equals(entry.getKey())) {
				throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
			}

			map.put(entry.getKey(), Ingredient.fromJson(entry.getValue()));
		}

		map.put(" ", Ingredient.EMPTY);
		return map;
	}

	public static class SerializerKJS extends AbstractRecipeSerializer<ShapedKubeJSRecipe> {
		@Override
		public ShapedKubeJSRecipe fromJson(ResourceLocation id, JsonObject json) {
			ShapedKubeJSRecipe r = new ShapedKubeJSRecipe(id);
			r.mirror = !json.has("mirror") || json.get("mirror").getAsBoolean();
			r.shrink = !json.has("shrink") || json.get("shrink").getAsBoolean();
			r.group = GsonHelper.getAsString(json, "group", "");
			Map<String, Ingredient> key = ShapedKubeJSRecipe.keyFromJson(GsonHelper.getAsJsonObject(json, "key"));
			String[] pattern = ShapedKubeJSRecipe.patternFromJson(GsonHelper.getAsJsonArray(json, "pattern"));

			if (r.shrink) {
				pattern = shrink(pattern);
			}

			r.width = pattern[0].length();
			r.height = pattern.length;
			r.ingredients = ShapedKubeJSRecipe.dissolvePattern(pattern, key, r.width, r.height);
			r.result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
			r.ingredientActions = IngredientAction.parseList(json.get("kubejs_actions"));
			return r;
		}

		@Override
		public ShapedKubeJSRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
			ShapedKubeJSRecipe r = new ShapedKubeJSRecipe(id);
			r.group = buf.readUtf(32767);
			r.width = buf.readVarInt();
			r.height = buf.readVarInt();
			r.ingredients = NonNullList.withSize(r.width * r.height, Ingredient.EMPTY);

			for (int i = 0; i < r.width * r.height; ++i) {
				r.ingredients.set(i, Ingredient.fromNetwork(buf));
			}

			r.result = buf.readItem();
			r.mirror = buf.readBoolean();
			r.shrink = buf.readBoolean();
			r.ingredientActions = IngredientAction.readList(buf);
			return r;
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf, ShapedKubeJSRecipe r) {
			buf.writeUtf(r.group);
			buf.writeVarInt(r.width);
			buf.writeVarInt(r.height);

			for (var ingredient : r.ingredients) {
				ingredient.toNetwork(buf);
			}

			buf.writeItem(r.result);
			buf.writeBoolean(r.mirror);
			buf.writeBoolean(r.shrink);
			IngredientAction.writeList(buf, r.ingredientActions);
		}
	}
}
