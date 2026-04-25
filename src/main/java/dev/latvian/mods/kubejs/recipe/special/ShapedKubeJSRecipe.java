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
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

import java.util.List;

public class ShapedKubeJSRecipe extends ShapedRecipe implements KubeJSCraftingRecipe {
	public static final MapCodec<ShapedKubeJSRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		CommonInfo.MAP_CODEC.forGetter(ShapedKubeJSRecipe::commonInfo),
		CraftingBookInfo.MAP_CODEC.forGetter(ShapedKubeJSRecipe::bookInfo),
		ShapedRecipePattern.MAP_CODEC.forGetter(ShapedKubeJSRecipe::pattern),
		ItemStackTemplate.CODEC.fieldOf("result").forGetter(ShapedKubeJSRecipe::result),
		Codec.BOOL.optionalFieldOf(MIRROR_KEY, true).forGetter(ShapedKubeJSRecipe::kjs$getMirror),
		IngredientActionHolder.LIST_CODEC.optionalFieldOf(INGREDIENT_ACTIONS_KEY, List.of()).forGetter(ShapedKubeJSRecipe::kjs$getIngredientActions),
		Codec.STRING.optionalFieldOf(MODIFY_RESULT_KEY, "").forGetter(ShapedKubeJSRecipe::kjs$getModifyResult)
	).apply(instance, ShapedKubeJSRecipe::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ShapedKubeJSRecipe> STREAM_CODEC = KubeJSStreamCodecs.composite(
		CommonInfo.STREAM_CODEC, ShapedKubeJSRecipe::commonInfo,
		CraftingBookInfo.STREAM_CODEC, ShapedKubeJSRecipe::bookInfo,
		ShapedRecipePattern.STREAM_CODEC, ShapedKubeJSRecipe::pattern,
		ItemStackTemplate.STREAM_CODEC, ShapedKubeJSRecipe::result,
		ByteBufCodecs.BOOL, ShapedKubeJSRecipe::kjs$getMirror,
		IngredientActionHolder.LIST_STREAM_CODEC, ShapedKubeJSRecipe::kjs$getIngredientActions,
		ByteBufCodecs.STRING_UTF8.cast(), ShapedKubeJSRecipe::kjs$getModifyResult,
		ShapedKubeJSRecipe::new
	);
	public static final RecipeSerializer<?> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);

	private final boolean mirror;
	private final List<IngredientActionHolder> ingredientActions;
	private final String modifyResult;

	public ShapedKubeJSRecipe(CommonInfo commonInfo, CraftingBookInfo bookInfo, ShapedRecipePattern pattern, ItemStackTemplate result, boolean mirror, List<IngredientActionHolder> ingredientActions, String modifyResult) {
		super(commonInfo, bookInfo, pattern, result);
		this.mirror = mirror;
		this.ingredientActions = ingredientActions;
		this.modifyResult = modifyResult;

		if (!mirror) {
			this.pattern.symmetrical = true;
		}
	}

	@Override
	public RecipeSerializer<ShapedRecipe> getSerializer() {
		return (RecipeSerializer<ShapedRecipe>) KubeJSRecipeSerializers.SHAPED.get();
	}

	@Override
	public List<IngredientActionHolder> kjs$getIngredientActions() {
		return ingredientActions;
	}

	@Override
	public String kjs$getModifyResult() {
		return modifyResult;
	}

	public boolean kjs$getMirror() {
		return mirror;
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

	private ShapedRecipePattern pattern() {
		return pattern;
	}

	private ItemStackTemplate result() {
		return result;
	}

	public ItemStack getResultItem() {
		return result.create();
	}
}