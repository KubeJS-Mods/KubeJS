package dev.latvian.mods.kubejs.recipe.special;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.architectury.core.AbstractRecipeSerializer;
import dev.latvian.mods.kubejs.recipe.KubeJSRecipeEventHandler;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientAction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;

import java.util.List;

public class ShapelessKubeJSRecipe extends ShapelessRecipe {
	private String group;
	private ItemStack result;
	private NonNullList<Ingredient> ingredients;
	private List<IngredientAction> ingredientActions;

	public ShapelessKubeJSRecipe(ResourceLocation _id) {
		super(_id, "", ItemStack.EMPTY, NonNullList.withSize(0, Ingredient.EMPTY));
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return KubeJSRecipeEventHandler.SHAPELESS.get();
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
	public boolean matches(CraftingContainer craftingContainer, Level level) {
		StackedContents stackedContents = new StackedContents();
		int count = 0;

		for (int i = 0; i < craftingContainer.getContainerSize(); i++) {
			ItemStack stack = craftingContainer.getItem(i);

			if (!stack.isEmpty()) {
				count++;
				stackedContents.accountStack(stack, 1);
			}
		}

		return count == ingredients.size() && stackedContents.canCraft(this, null);
	}

	@Override
	public ItemStack assemble(CraftingContainer craftingContainer) {
		return result.copy();
	}

	@Override
	public boolean canCraftInDimensions(int w, int h) {
		return w * h >= ingredients.size();
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
		NonNullList<ItemStack> list = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);

		for (int i = 0; i < list.size(); i++) {
			list.set(i, IngredientAction.getRemaining(container, i, ingredientActions));
		}

		return list;
	}

	public static class SerializerKJS extends AbstractRecipeSerializer<ShapelessKubeJSRecipe> {
		@Override
		public ShapelessKubeJSRecipe fromJson(ResourceLocation id, JsonObject json) {
			ShapelessKubeJSRecipe r = new ShapelessKubeJSRecipe(id);
			r.group = GsonHelper.getAsString(json, "group", "");
			r.ingredients = itemsFromJson(GsonHelper.getAsJsonArray(json, "ingredients"));

			if (r.ingredients.isEmpty()) {
				throw new JsonParseException("No ingredients for shapeless recipe");
			} else if (r.ingredients.size() > 9) {
				throw new JsonParseException("Too many ingredients for shapeless recipe");
			}

			r.result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
			r.ingredientActions = IngredientAction.parseList(json.get("kubejs_actions"));
			return r;
		}

		private static NonNullList<Ingredient> itemsFromJson(JsonArray a) {
			NonNullList<Ingredient> list = NonNullList.create();

			for (int i = 0; i < a.size(); i++) {
				Ingredient ingredient = Ingredient.fromJson(a.get(i));

				if (!ingredient.isEmpty()) {
					list.add(ingredient);
				}
			}

			return list;
		}

		@Override
		public ShapelessKubeJSRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
			ShapelessKubeJSRecipe r = new ShapelessKubeJSRecipe(id);
			r.group = buf.readUtf(32767);
			int s = buf.readVarInt();
			r.ingredients = NonNullList.withSize(s, Ingredient.EMPTY);

			for (int i = 0; i < s; ++i) {
				r.ingredients.set(i, Ingredient.fromNetwork(buf));
			}

			r.result = buf.readItem();
			r.ingredientActions = IngredientAction.readList(buf);
			return r;
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf, ShapelessKubeJSRecipe r) {
			buf.writeUtf(r.group);
			buf.writeVarInt(r.ingredients.size());

			for (var ingredient : r.ingredients) {
				ingredient.toNetwork(buf);
			}

			buf.writeItem(r.result);
			IngredientAction.writeList(buf, r.ingredientActions);
		}
	}
}
