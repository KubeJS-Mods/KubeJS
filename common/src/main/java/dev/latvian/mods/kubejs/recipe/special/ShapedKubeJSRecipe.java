package dev.latvian.mods.kubejs.recipe.special;

import com.google.gson.JsonObject;
import com.mojang.util.UUIDTypeAdapter;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.recipe.KubeJSRecipeEventHandler;
import dev.latvian.mods.kubejs.recipe.ModifyRecipeCraftingGrid;
import dev.latvian.mods.kubejs.recipe.ModifyRecipeResultCallback;
import dev.latvian.mods.kubejs.recipe.RecipesEventJS;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientAction;
import dev.latvian.mods.kubejs.util.UtilsJS;
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

import javax.annotation.Nullable;
import java.util.List;

public class ShapedKubeJSRecipe extends ShapedRecipe {

	private final boolean mirror;
	private final List<IngredientAction> ingredientActions;
	private final ModifyRecipeResultCallback modifyResult;

	public ShapedKubeJSRecipe(ResourceLocation id, String group, int width, int height, NonNullList<Ingredient> ingredients, ItemStack result,
							  boolean mirror, List<IngredientAction> ingredientActions, @Nullable ModifyRecipeResultCallback modifyResult) {
		super(id, group, width, height, ingredients, result);
		this.mirror = mirror;
		this.ingredientActions = ingredientActions;
		this.modifyResult = modifyResult;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return KubeJSRecipeEventHandler.SHAPED.get();
	}

	@Override
	public boolean matches(CraftingContainer craftingContainer, Level level) {
		for (var i = 0; i <= craftingContainer.getWidth() - this.width; ++i) {
			for (var j = 0; j <= craftingContainer.getHeight() - this.height; ++j) {
				if (mirror && this.matches(craftingContainer, i, j, true)) {
					return true;
				}

				if (this.matches(craftingContainer, i, j, false)) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public ItemStack assemble(CraftingContainer container) {
		if (modifyResult != null) {
			return modifyResult.modify(new ModifyRecipeCraftingGrid(container), ItemStackJS.of(getResultItem().copy())).getItemStack();
		}

		return getResultItem().copy();
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
		var list = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);

		for (var i = 0; i < list.size(); i++) {
			list.set(i, IngredientAction.getRemaining(container, i, ingredientActions));
		}

		return list;
	}

	public static class SerializerKJS implements RecipeSerializer<ShapedKubeJSRecipe> {

		// registry replacement... you never know
		private static final RecipeSerializer<ShapedRecipe> SHAPED = UtilsJS.cast(KubeJSRegistries.recipeSerializers().get(new ResourceLocation("crafting_shaped")));

		@Override
		public ShapedKubeJSRecipe fromJson(ResourceLocation id, JsonObject json) {
			var shapedRecipe = SHAPED.fromJson(id, json);

			var mirror = GsonHelper.getAsBoolean(json, "mirror", true);
			var shrink = GsonHelper.getAsBoolean(json, "shrink", true);

			// it sucks that we can't reuse the ShapedRecipe directly here,
			// but the pattern is shrunk automatically, so we need to recreate it
			// TODO: maybe these classes *would* be better off as a mixin, after all
			var key = ShapedRecipe.keyFromJson(GsonHelper.getAsJsonObject(json, "key"));
			var pattern = ShapedRecipe.patternFromJson(GsonHelper.getAsJsonArray(json, "pattern"));

			if (shrink) {
				pattern = ShapedRecipe.shrink(pattern);
			}

			int w = pattern[0].length(), h = pattern.length;
			var ingredients = ShapedRecipe.dissolvePattern(pattern, key, w, h);

			var ingredientActions = IngredientAction.parseList(json.get("kubejs_actions"));

			ModifyRecipeResultCallback modifyResult = null;
			if (json.has("kubejs_modify_result")) {
				modifyResult = RecipesEventJS.modifyResultCallbackMap.get(UUIDTypeAdapter.fromString(json.get("kubejs_modify_result").getAsString()));
			}

			return new ShapedKubeJSRecipe(id, shapedRecipe.getGroup(), w, h, ingredients, shapedRecipe.getResultItem(), mirror, ingredientActions, modifyResult);
		}

		@Override
		public ShapedKubeJSRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
			var shapedRecipe = SHAPED.fromNetwork(id, buf);
			var mirror = buf.readBoolean();
			var ingredientActions = IngredientAction.readList(buf);

			// original values
			var group = shapedRecipe.getGroup();
			var width = shapedRecipe.getWidth();
			var height = shapedRecipe.getHeight();
			var ingredients = shapedRecipe.getIngredients();
			var result = shapedRecipe.getResultItem();

			// the pattern can be used as-is because the shrinking logic is done serverside
			// additionally, result modification callbacks do not need to be synced to clients
			return new ShapedKubeJSRecipe(id, group, width, height, ingredients, result, mirror, ingredientActions, null);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf, ShapedKubeJSRecipe r) {
			SHAPED.toNetwork(buf, r);
			buf.writeBoolean(r.mirror);
			IngredientAction.writeList(buf, r.ingredientActions);
		}
	}
}
