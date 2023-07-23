package dev.latvian.mods.kubejs.recipe.special;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.recipe.KubeJSRecipeEventHandler;
import dev.latvian.mods.kubejs.recipe.ModifyRecipeResultCallback;
import dev.latvian.mods.kubejs.recipe.RecipesEventJS;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientAction;
import dev.latvian.mods.kubejs.registry.KubeJSRegistries;
import dev.latvian.mods.kubejs.stages.predicate.StagePredicate;
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
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class ShapedKubeJSRecipe extends ShapedRecipe implements KubeJSCraftingRecipe {

	private final boolean mirror;
	private final List<IngredientAction> ingredientActions;
	private final ModifyRecipeResultCallback modifyResult;
	@Nullable
	private final StagePredicate stage;

	public ShapedKubeJSRecipe(ResourceLocation id, String group, int width, int height, NonNullList<Ingredient> ingredients, ItemStack result,
	                          boolean mirror, List<IngredientAction> ingredientActions, @Nullable ModifyRecipeResultCallback modifyResult, @Nullable StagePredicate stage) {
		super(id, group, width, height, ingredients, result);
		this.mirror = mirror;
		this.ingredientActions = ingredientActions;
		this.modifyResult = modifyResult;
		this.stage = stage;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return KubeJSRecipeEventHandler.SHAPED.get();
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
	public StagePredicate kjs$getStage() {
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

	public static class SerializerKJS implements RecipeSerializer<ShapedKubeJSRecipe> {

		// registry replacement... you never know
		private static final RecipeSerializer<ShapedRecipe> SHAPED = UtilsJS.cast(KubeJSRegistries.recipeSerializers().get(new ResourceLocation("crafting_shaped")));

		@Override
		public ShapedKubeJSRecipe fromJson(ResourceLocation id, JsonObject json) {
			var shapedRecipe = SHAPED.fromJson(id, json);

			var mirror = GsonHelper.getAsBoolean(json, "kubejs:mirror", true);
			var shrink = GsonHelper.getAsBoolean(json, "kubejs:shrink", true);

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

			var ingredientActions = IngredientAction.parseList(json.get("kubejs:actions"));

			ModifyRecipeResultCallback modifyResult = null;
			if (json.has("kubejs:modify_result")) {
				modifyResult = RecipesEventJS.MODIFY_RESULT_CALLBACKS.get(id);
			}

			StagePredicate stage = null;
			if (json.has("kubejs:stage")) {
				stage = StagePredicate.fromJson(json.get("kubejs:stage"));
			}

			return new ShapedKubeJSRecipe(id, shapedRecipe.getGroup(), w, h, ingredients, shapedRecipe.getResultItem(), mirror, ingredientActions, modifyResult, stage);
		}

		@Override
		public ShapedKubeJSRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
			var shapedRecipe = SHAPED.fromNetwork(id, buf);
			var flags = (int) buf.readByte();

			// original values
			var group = shapedRecipe.getGroup();
			var width = shapedRecipe.getWidth();
			var height = shapedRecipe.getHeight();
			var ingredients = shapedRecipe.getIngredients();
			var result = shapedRecipe.getResultItem();

			List<IngredientAction> ingredientActions = (flags & RecipeFlags.INGREDIENT_ACTIONS) != 0 ? IngredientAction.readList(buf) : Collections.emptyList();
			var stage = (flags & RecipeFlags.STAGE) != 0 ? StagePredicate.fromNetwork(buf) : null;
			var mirror = (flags & RecipeFlags.MIRROR) != 0;

			// the pattern can be used as-is because the shrinking logic is done serverside
			// additionally, result modification callbacks do not need to be synced to clients
			return new ShapedKubeJSRecipe(id, group, width, height, ingredients, result, mirror, ingredientActions, null, stage);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf, ShapedKubeJSRecipe r) {
			SHAPED.toNetwork(buf, r);

			int flags = 0;

			if (r.ingredientActions != null && !r.ingredientActions.isEmpty()) {
				flags |= RecipeFlags.INGREDIENT_ACTIONS;
			}

			if (r.mirror) {
				flags |= RecipeFlags.MIRROR;
			}

			if (r.stage != null) {
				flags |= RecipeFlags.STAGE;
			}

			buf.writeByte(flags);

			if (r.ingredientActions != null && !r.ingredientActions.isEmpty()) {
				IngredientAction.writeList(buf, r.ingredientActions);
			}

			if (r.stage != null) {
				r.stage.toNetwork(buf);
			}
		}
	}
}
