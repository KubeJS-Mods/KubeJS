package dev.latvian.mods.kubejs.recipe.special;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.KubeJSStreamCodecs;
import dev.latvian.mods.kubejs.recipe.KubeJSRecipeSerializers;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientActionHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

import java.util.List;

public class ShapedKubeJSRecipe extends ShapedRecipe implements KubeJSCraftingRecipe {
	private final boolean mirror;
	private final List<IngredientActionHolder> ingredientActions;
	private final String modifyResult;
	private final String stage;

	public ShapedKubeJSRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result, boolean showNotification, boolean mirror, List<IngredientActionHolder> ingredientActions, String modifyResult, String stage) {
		super(group, category, pattern, result, showNotification);
		this.mirror = mirror;
		this.ingredientActions = ingredientActions;
		this.modifyResult = modifyResult;
		this.stage = stage;

		if (!mirror) {
			this.pattern.symmetrical = true;
		}
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return KubeJSRecipeSerializers.SHAPED.get();
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
	public String kjs$getStage() {
		return stage;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
		return kjs$getRemainingItems(input);
	}

	@Override
	public ItemStack assemble(CraftingInput input, HolderLookup.Provider registryAccess) {
		return kjs$assemble(input, registryAccess);
	}

	private ShapedRecipePattern pattern() {
		return pattern;
	}

	private ItemStack result() {
		return result;
	}

	public static class SerializerKJS implements RecipeSerializer<ShapedKubeJSRecipe> {
		public static final MapCodec<ShapedKubeJSRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.STRING.optionalFieldOf("group", "").forGetter(ShapedRecipe::getGroup),
			CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(ShapedRecipe::category),
			// KubeJS modified keys
			ShapedRecipePattern.MAP_CODEC.forGetter(recipe -> recipe.pattern),
			ItemStack.STRICT_CODEC.fieldOf("result").forGetter(r -> r.result),
			Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(ShapedRecipe::showNotification),
			// KubeJS additions
			Codec.BOOL.optionalFieldOf(MIRROR_KEY, true).forGetter(ShapedKubeJSRecipe::kjs$getMirror),
			IngredientActionHolder.LIST_CODEC.optionalFieldOf(INGREDIENT_ACTIONS_KEY, List.of()).forGetter(ShapedKubeJSRecipe::kjs$getIngredientActions),
			Codec.STRING.optionalFieldOf(MODIFY_RESULT_KEY, "").forGetter(ShapedKubeJSRecipe::kjs$getModifyResult),
			Codec.STRING.optionalFieldOf(STAGE_KEY, "").forGetter(ShapedKubeJSRecipe::kjs$getStage)
		).apply(instance, ShapedKubeJSRecipe::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, ShapedKubeJSRecipe> STREAM_CODEC = KubeJSStreamCodecs.composite(
			ByteBufCodecs.STRING_UTF8,
			ShapedKubeJSRecipe::getGroup,
			CraftingBookCategory.STREAM_CODEC,
			ShapedKubeJSRecipe::category,
			ShapedRecipePattern.STREAM_CODEC,
			ShapedKubeJSRecipe::pattern,
			// KubeJS modified keys
			ItemStack.STREAM_CODEC,
			ShapedKubeJSRecipe::result,
			ByteBufCodecs.BOOL,
			ShapedKubeJSRecipe::showNotification,
			// KubeJS additions
			ByteBufCodecs.BOOL,
			ShapedKubeJSRecipe::kjs$getMirror,
			IngredientActionHolder.LIST_STREAM_CODEC,
			ShapedKubeJSRecipe::kjs$getIngredientActions,
			ByteBufCodecs.STRING_UTF8,
			ShapedKubeJSRecipe::kjs$getModifyResult,
			ByteBufCodecs.STRING_UTF8,
			ShapedKubeJSRecipe::kjs$getStage,
			ShapedKubeJSRecipe::new
		);

		@Override
		public MapCodec<ShapedKubeJSRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, ShapedKubeJSRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
