package dev.latvian.mods.kubejs.recipe.special;

import com.google.gson.JsonObject;
import com.mojang.util.UUIDTypeAdapter;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.recipe.KubeJSRecipeEventHandler;
import dev.latvian.mods.kubejs.recipe.ModifyRecipeCraftingGrid;
import dev.latvian.mods.kubejs.recipe.ModifyRecipeResultCallback;
import dev.latvian.mods.kubejs.recipe.RecipeEventJS;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientAction;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import javax.annotation.Nullable;
import java.util.List;

public class ShapelessKubeJSRecipe extends ShapelessRecipe {
	private final List<IngredientAction> ingredientActions;
	private final ModifyRecipeResultCallback modifyResult;

	public ShapelessKubeJSRecipe(ShapelessRecipe original, List<IngredientAction> ingredientActions, @Nullable ModifyRecipeResultCallback modifyResult) {
		super(original.getId(), original.getGroup(), original.getResultItem(), original.getIngredients());
		this.ingredientActions = ingredientActions;
		this.modifyResult = modifyResult;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return KubeJSRecipeEventHandler.SHAPELESS.get();
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

	public static class SerializerKJS implements RecipeSerializer<ShapelessKubeJSRecipe> {

		// registry replacement... you never know
		private static final RecipeSerializer<ShapelessRecipe> SHAPELESS = UtilsJS.cast(KubeJSRegistries.recipeSerializers().get(new ResourceLocation("crafting_shapeless")));

		@Override
		public ShapelessKubeJSRecipe fromJson(ResourceLocation id, JsonObject json) {
			var shapelessRecipe = SHAPELESS.fromJson(id, json);

			var ingredientActions = IngredientAction.parseList(json.get("kubejs_actions"));
			ModifyRecipeResultCallback modifyResult = null;
			if (json.has("kubejs_modify_result")) {
				modifyResult = RecipeEventJS.modifyResultCallbackMap.get(UUIDTypeAdapter.fromString(json.get("kubejs_modify_result").getAsString()));
			}

			return new ShapelessKubeJSRecipe(shapelessRecipe, ingredientActions, modifyResult);
		}

		@Override
		public ShapelessKubeJSRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
			var shapelessRecipe = SHAPELESS.fromNetwork(id, buf);
			var ingredientActions = IngredientAction.readList(buf);

			// result modification callbacks do not need to be synced to clients
			return new ShapelessKubeJSRecipe(shapelessRecipe, ingredientActions, null);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf, ShapelessKubeJSRecipe r) {
			SHAPELESS.toNetwork(buf, r);
			IngredientAction.writeList(buf, r.ingredientActions);
		}
	}
}
