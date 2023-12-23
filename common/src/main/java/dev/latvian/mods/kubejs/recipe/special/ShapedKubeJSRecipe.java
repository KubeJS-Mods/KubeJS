package dev.latvian.mods.kubejs.recipe.special;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.latvian.mods.kubejs.recipe.KubeJSRecipeEventHandler;
import dev.latvian.mods.kubejs.recipe.ModifyRecipeResultCallback;
import dev.latvian.mods.kubejs.recipe.RecipesEventJS;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientAction;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ShapedKubeJSRecipe extends ShapedRecipe implements KubeJSCraftingRecipe {

	private final boolean mirror;
	private final List<IngredientAction> ingredientActions;
	private final ModifyRecipeResultCallback modifyResult;
	private final String stage;

	// TODO: All of the hell that is ShapedRecipePattern
	public ShapedKubeJSRecipe(String group, CraftingBookCategory category, int width, int height, NonNullList<Ingredient> ingredients, ItemStack result,
							  boolean mirror, List<IngredientAction> ingredientActions, @Nullable ModifyRecipeResultCallback modifyResult, String stage) {
		super(group, category, width, height, ingredients, result);
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
	public String kjs$getStage() {
		return stage;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
		return kjs$getRemainingItems(container);
	}

	@Override
	public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
		return kjs$assemble(container, registryAccess);
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

		// registry replacement-safe(?)
		private static final RecipeSerializer<ShapedRecipe> SHAPED = UtilsJS.cast(RegistryInfo.RECIPE_SERIALIZER.getValue(new ResourceLocation("crafting_shaped")));

		private static final MapCodec.MapCodecCodec<ShapedRecipe> SHAPED_CODEC = getCodec();

		private static MapCodec.MapCodecCodec<ShapedRecipe> getCodec() {
			try {
				return UtilsJS.cast(SHAPED_CODEC);
			} catch (ClassCastException e) {
				throw new IllegalStateException("Original ShapedRecipe codec is not a MapCodecCodec!");
			}
		}

		// FIXME: i am in great pain
		public static final Codec<ShapedKubeJSRecipe> CODEC = null;

		@Override
		public Codec<ShapedKubeJSRecipe> codec() {
			return CODEC;
		}

		public ShapedKubeJSRecipe fromJson(ResourceLocation id, JsonObject json) {
			var shapedRecipe = SHAPED.fromJson(id, json);

			var mirror = GsonHelper.getAsBoolean(json, "kubejs:mirror", true);
			var shrink = GsonHelper.getAsBoolean(json, "kubejs:shrink", true);

			// it sucks that we can't reuse the ShapedRecipe directly here,
			// but the pattern is shrunk automatically, so we need to recreate it
			// TODO: maybe these classes *would* be better off as a mixin, after all
			//  added note 02/08/2023: **especially** with MixinExtras now, this should be possible!
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

			var stage = GsonHelper.getAsString(json, "kubejs:stage", "");

			return new ShapedKubeJSRecipe(id, shapedRecipe.getGroup(), shapedRecipe.category(), w, h, ingredients, shapedRecipe.result, mirror, ingredientActions, modifyResult, stage);
		}

		@Override
		public ShapedKubeJSRecipe fromNetwork(FriendlyByteBuf buf) {
			var shapedRecipe = SHAPED.fromNetwork(buf);
			var flags = (int) buf.readByte();

			// original values
			var group = shapedRecipe.getGroup();
			var category = shapedRecipe.category();
			var width = shapedRecipe.getWidth();
			var height = shapedRecipe.getHeight();
			var ingredients = shapedRecipe.getIngredients();
			var result = shapedRecipe.result;

			List<IngredientAction> ingredientActions = (flags & RecipeFlags.INGREDIENT_ACTIONS) != 0 ? IngredientAction.readList(buf) : List.of();
			var stage = (flags & RecipeFlags.STAGE) != 0 ? buf.readUtf() : "";
			var mirror = (flags & RecipeFlags.MIRROR) != 0;

			// the pattern can be used as-is because the shrinking logic is done serverside
			// additionally, result modification callbacks do not need to be synced to clients
			return new ShapedKubeJSRecipe(group, category, width, height, ingredients, result, mirror, ingredientActions, null, stage);
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
