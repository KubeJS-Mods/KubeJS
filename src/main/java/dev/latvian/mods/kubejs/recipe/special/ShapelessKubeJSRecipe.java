package dev.latvian.mods.kubejs.recipe.special;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.recipe.KubeJSRecipeSerializers;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientActionHolder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.List;

public class ShapelessKubeJSRecipe extends ShapelessRecipe implements KubeJSCraftingRecipe {
	public static final MapCodec<ShapelessKubeJSRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		CommonInfo.MAP_CODEC.forGetter(ShapelessKubeJSRecipe::commonInfo),
		CraftingBookInfo.MAP_CODEC.forGetter(ShapelessKubeJSRecipe::bookInfo),
		ItemStackTemplate.CODEC.fieldOf("result").forGetter(r -> r.result),
		Codec.lazyInitialized(() -> Ingredient.CODEC.listOf(1, ShapedRecipePattern.getMaxHeight() * ShapedRecipePattern.getMaxWidth()))
			.fieldOf("ingredients").forGetter(r -> r.ingredients),
		IngredientActionHolder.LIST_CODEC.optionalFieldOf(INGREDIENT_ACTIONS_KEY, List.of()).forGetter(ShapelessKubeJSRecipe::kjs$getIngredientActions),
		Codec.STRING.optionalFieldOf(MODIFY_RESULT_KEY, "").forGetter(ShapelessKubeJSRecipe::kjs$getModifyResult)
	).apply(instance, ShapelessKubeJSRecipe::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ShapelessKubeJSRecipe> STREAM_CODEC = StreamCodec.composite(
		CommonInfo.STREAM_CODEC, ShapelessKubeJSRecipe::commonInfo,
		CraftingBookInfo.STREAM_CODEC, ShapelessKubeJSRecipe::bookInfo,
		ItemStackTemplate.STREAM_CODEC, r -> r.result,
		Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), r -> r.ingredients,
		IngredientActionHolder.LIST_STREAM_CODEC, ShapelessKubeJSRecipe::kjs$getIngredientActions,
		ByteBufCodecs.STRING_UTF8.cast(), ShapelessKubeJSRecipe::kjs$getModifyResult,
		ShapelessKubeJSRecipe::new
	);
	public static final RecipeSerializer<ShapelessKubeJSRecipe> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);

	private final List<IngredientActionHolder> ingredientActions;
	private final String modifyResult;

	public ShapelessKubeJSRecipe(CommonInfo commonInfo, CraftingBookInfo bookInfo, ItemStackTemplate result, List<Ingredient> ingredients, List<IngredientActionHolder> ingredientActions, String modifyResult) {
		super(commonInfo, bookInfo, result, ingredients);
		this.ingredientActions = ingredientActions;
		this.modifyResult = modifyResult;
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
	public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
		return kjs$getRemainingItems(input);
	}

	@Override
	public ItemStack assemble(CraftingInput input) {
		return kjs$assemble(input, super.assemble(input));
	}

	private CommonInfo commonInfo() {
		return commonInfo;
	}

	private CraftingBookInfo bookInfo() {
		return bookInfo;
	}
}