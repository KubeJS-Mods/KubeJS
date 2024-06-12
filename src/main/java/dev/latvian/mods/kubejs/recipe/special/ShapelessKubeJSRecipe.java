package dev.latvian.mods.kubejs.recipe.special;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.recipe.KubeJSRecipeSerializers;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientActionHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.List;

public class ShapelessKubeJSRecipe extends ShapelessRecipe implements KubeJSCraftingRecipe {
	private final List<IngredientActionHolder> ingredientActions;
	private final String modifyResult;
	private final String stage;

	public ShapelessKubeJSRecipe(ShapelessRecipe original, List<IngredientActionHolder> ingredientActions, String modifyResult, String stage) {
		super(original.getGroup(), original.category(), original.result, original.getIngredients());
		this.ingredientActions = ingredientActions;
		this.modifyResult = modifyResult;
		this.stage = stage;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return KubeJSRecipeSerializers.SHAPELESS.get();
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
	public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
		return kjs$getRemainingItems(container);
	}

	@Override
	public ItemStack assemble(CraftingContainer container, HolderLookup.Provider registryAccess) {
		return kjs$assemble(container, registryAccess);
	}

	public static class SerializerKJS implements RecipeSerializer<ShapelessKubeJSRecipe> {
		public static final MapCodec<ShapelessKubeJSRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Serializer.CODEC.forGetter(r -> r),
			IngredientActionHolder.LIST_CODEC.optionalFieldOf(INGREDIENT_ACTIONS_KEY, List.of()).forGetter(ShapelessKubeJSRecipe::kjs$getIngredientActions),
			Codec.STRING.optionalFieldOf(MODIFY_RESULT_KEY, "").forGetter(ShapelessKubeJSRecipe::kjs$getModifyResult),
			Codec.STRING.optionalFieldOf(STAGE_KEY, "").forGetter(ShapelessKubeJSRecipe::kjs$getStage)
		).apply(instance, ShapelessKubeJSRecipe::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, ShapelessKubeJSRecipe> STREAM_CODEC = StreamCodec.composite(
			Serializer.STREAM_CODEC, r -> r,
			IngredientActionHolder.LIST_STREAM_CODEC, ShapelessKubeJSRecipe::kjs$getIngredientActions,
			ByteBufCodecs.STRING_UTF8, ShapelessKubeJSRecipe::kjs$getModifyResult,
			ByteBufCodecs.STRING_UTF8, ShapelessKubeJSRecipe::kjs$getStage,
			ShapelessKubeJSRecipe::new
		);

		@Override
		public MapCodec<ShapelessKubeJSRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, ShapelessKubeJSRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
