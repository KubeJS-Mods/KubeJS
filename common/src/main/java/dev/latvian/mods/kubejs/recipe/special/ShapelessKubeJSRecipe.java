package dev.latvian.mods.kubejs.recipe.special;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.recipe.KubeJSRecipeEventHandler;
import dev.latvian.mods.kubejs.recipe.ModifyRecipeResultCallback;
import dev.latvian.mods.kubejs.recipe.RecipesEventJS;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientAction;
import dev.latvian.mods.kubejs.registry.KubeJSRegistries;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class ShapelessKubeJSRecipe extends ShapelessRecipe implements KubeJSCraftingRecipe {
	private final List<IngredientAction> ingredientActions;
	private final ModifyRecipeResultCallback modifyResult;
	private final String stage;

	public ShapelessKubeJSRecipe(ShapelessRecipe original, List<IngredientAction> ingredientActions, @Nullable ModifyRecipeResultCallback modifyResult, String stage) {
		super(original.getId(), original.getGroup(), original.getResultItem(), original.getIngredients());
		this.ingredientActions = ingredientActions;
		this.modifyResult = modifyResult;
		this.stage = stage;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return KubeJSRecipeEventHandler.SHAPELESS.get();
	}

	@Override
	public List<IngredientAction> kjs$getIngredientActions() {
		return ingredientActions;
	}

	@Override
	@Nullable
	public ModifyRecipeResultCallback kjs$getModifyResult() {
		return modifyResult;
	}

	@Override
	public String kjs$getStage() {
		return stage;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
		return kjs$getRemainingItems(container);
	}

	@Override
	public ItemStack assemble(CraftingContainer container) {
		return kjs$assemble(container);
	}

	public static class SerializerKJS implements RecipeSerializer<ShapelessKubeJSRecipe> {

		// registry replacement... you never know
		private static final RecipeSerializer<ShapelessRecipe> SHAPELESS = UtilsJS.cast(KubeJSRegistries.recipeSerializers().get(new ResourceLocation("crafting_shapeless")));

		@Override
		public ShapelessKubeJSRecipe fromJson(ResourceLocation id, JsonObject json) {
			var shapelessRecipe = SHAPELESS.fromJson(id, json);

			var ingredientActions = IngredientAction.parseList(json.get("kubejs:actions"));
			ModifyRecipeResultCallback modifyResult = null;
			if (json.has("kubejs:modify_result")) {
				modifyResult = RecipesEventJS.MODIFY_RESULT_CALLBACKS.get(id);
			}

			var stage = GsonHelper.getAsString(json, "kubejs:stage", "");

			return new ShapelessKubeJSRecipe(shapelessRecipe, ingredientActions, modifyResult, stage);
		}

		@Override
		public ShapelessKubeJSRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
			var shapelessRecipe = SHAPELESS.fromNetwork(id, buf);
			var flags = (int) buf.readByte();

			List<IngredientAction> ingredientActions = (flags & RecipeFlags.INGREDIENT_ACTIONS) != 0 ? IngredientAction.readList(buf) : List.of();
			var stage = (flags & RecipeFlags.STAGE) != 0 ? buf.readUtf() : "";

			// result modification callbacks do not need to be synced to clients
			return new ShapelessKubeJSRecipe(shapelessRecipe, ingredientActions, null, stage);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf, ShapelessKubeJSRecipe r) {
			SHAPELESS.toNetwork(buf, r);

			int flags = 0;

			if (r.ingredientActions != null && !r.ingredientActions.isEmpty()) {
				flags |= RecipeFlags.INGREDIENT_ACTIONS;
			}

			if (!r.stage.isEmpty()) {
				flags |= RecipeFlags.STAGE;
			}

			buf.writeByte(flags);

			if (r.ingredientActions != null && !r.ingredientActions.isEmpty()) {
				IngredientAction.writeList(buf, r.ingredientActions);
			}

			if (!r.stage.isEmpty()) {
				buf.writeUtf(r.stage);
			}
		}
	}
}
