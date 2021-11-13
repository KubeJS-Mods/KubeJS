package dev.latvian.kubejs.recipe.special;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.latvian.kubejs.recipe.KubeJSRecipeEventHandler;
import me.shedaniel.architectury.core.AbstractRecipeSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

public class ShapelessKubeJSRecipe implements CraftingRecipe {
	private final ResourceLocation id;
	private String group;
	private ItemStack result;
	private NonNullList<Ingredient> ingredients;

	public ShapelessKubeJSRecipe(ResourceLocation i) {
		id = i;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return KubeJSRecipeEventHandler.SHAPELESS.get();
	}

	@Override
	@Environment(EnvType.CLIENT)
	public String getGroup() {
		return this.group;
	}

	@Override
	public ItemStack getResultItem() {
		return this.result;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return this.ingredients;
	}

	@Override
	public boolean matches(CraftingContainer craftingContainer, Level level) {
		StackedContents stackedContents = new StackedContents();
		int i = 0;

		for (int j = 0; j < craftingContainer.getContainerSize(); ++j) {
			ItemStack itemStack = craftingContainer.getItem(j);
			if (!itemStack.isEmpty()) {
				++i;
				stackedContents.accountStack(itemStack, 1);
			}
		}

		return i == this.ingredients.size() && stackedContents.canCraft(this, null);
	}

	@Override
	public ItemStack assemble(CraftingContainer craftingContainer) {
		return this.result.copy();
	}

	@Override
	@Environment(EnvType.CLIENT)
	public boolean canCraftInDimensions(int i, int j) {
		return i * j >= this.ingredients.size();
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

			r.result = ShapedRecipe.itemFromJson(GsonHelper.getAsJsonObject(json, "result"));
			return r;
		}

		private static NonNullList<Ingredient> itemsFromJson(JsonArray a) {
			NonNullList<Ingredient> list = NonNullList.create();

			for (int i = 0; i < a.size(); ++i) {
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
			return r;
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf, ShapelessKubeJSRecipe r) {
			buf.writeUtf(r.group);
			buf.writeVarInt(r.ingredients.size());

			for (Ingredient ingredient : r.ingredients) {
				ingredient.toNetwork(buf);
			}

			buf.writeItem(r.result);
		}
	}
}
