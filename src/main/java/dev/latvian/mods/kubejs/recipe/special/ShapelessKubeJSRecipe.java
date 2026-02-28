package dev.latvian.mods.kubejs.recipe.special;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.codec.KubeJSStreamCodecs;
import dev.latvian.mods.kubejs.recipe.KubeJSRecipeSerializers;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientActionHolder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe.CraftingBookInfo;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe.CommonInfo;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.List;

public class ShapelessKubeJSRecipe extends ShapelessRecipe implements KubeJSCraftingRecipe {
	private final List<IngredientActionHolder> ingredientActions;
	private final String modifyResult;
	private final String stage;

	public ShapelessKubeJSRecipe(CommonInfo commonInfo, CraftingBookInfo bookInfo, net.minecraft.world.item.ItemStackTemplate result, List<Ingredient> ingredients, List<IngredientActionHolder> ingredientActions, String modifyResult, String stage) {
		super(commonInfo, bookInfo, result, ingredients);
		this.ingredientActions = ingredientActions;
		this.modifyResult = modifyResult;
		this.stage = stage;
	}

	@Override
	public RecipeSerializer<ShapelessRecipe> getSerializer() {
		return (RecipeSerializer<ShapelessRecipe>) KubeJSRecipeSerializers.SHAPELESS.get();
	}

	@Override
	public List<IngredientActionHolder> kjs$getIngredientActions() {
		return ingredientActions;
	}

	@Override
	public String kjs$getModifyResult() {
		return modifyResult;
	}

	@Override
	public String kjs$getStage() {
		return stage;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
		return kjs$getRemainingItems(input);
	}

	@Override
	public ItemStack assemble(CraftingInput input) {
		return kjs$assemble(input);
	}

	private CommonInfo commonInfo() {
		return commonInfo;
	}

	private CraftingBookInfo bookInfo() {
		return bookInfo;
	}

	public static class SerializerKJS {
		public static final MapCodec<ShapelessKubeJSRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			CommonInfo.MAP_CODEC.forGetter(ShapelessKubeJSRecipe::commonInfo),
			CraftingBookInfo.MAP_CODEC.forGetter(ShapelessKubeJSRecipe::bookInfo),
			net.minecraft.world.item.ItemStackTemplate.CODEC.fieldOf("result").forGetter(r -> r.result),
			Codec.lazyInitialized(() -> Ingredient.CODEC.listOf(1, ShapedRecipePattern.getMaxHeight() * ShapedRecipePattern.getMaxWidth()))
				.fieldOf("ingredients").forGetter(r -> r.ingredients),
			IngredientActionHolder.LIST_CODEC.optionalFieldOf(INGREDIENT_ACTIONS_KEY, List.of()).forGetter(ShapelessKubeJSRecipe::kjs$getIngredientActions),
			Codec.STRING.optionalFieldOf(MODIFY_RESULT_KEY, "").forGetter(ShapelessKubeJSRecipe::kjs$getModifyResult),
			Codec.STRING.optionalFieldOf(STAGE_KEY, "").forGetter(ShapelessKubeJSRecipe::kjs$getStage)
		).apply(instance, ShapelessKubeJSRecipe::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, ShapelessKubeJSRecipe> STREAM_CODEC = KubeJSStreamCodecs.composite(
			CommonInfo.STREAM_CODEC, ShapelessKubeJSRecipe::commonInfo,
			CraftingBookInfo.STREAM_CODEC, ShapelessKubeJSRecipe::bookInfo,
			ItemStackTemplate.STREAM_CODEC, r -> r.result,
			Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), r -> r.ingredients,
			IngredientActionHolder.LIST_STREAM_CODEC, ShapelessKubeJSRecipe::kjs$getIngredientActions,
			ByteBufCodecs.STRING_UTF8.cast(), ShapelessKubeJSRecipe::kjs$getModifyResult,
			ByteBufCodecs.STRING_UTF8.cast(), ShapelessKubeJSRecipe::kjs$getStage,
			ShapelessKubeJSRecipe::new
		);

		public MapCodec<ShapelessKubeJSRecipe> codec() {
			return CODEC;
		}

		public StreamCodec<RegistryFriendlyByteBuf, ShapelessKubeJSRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}