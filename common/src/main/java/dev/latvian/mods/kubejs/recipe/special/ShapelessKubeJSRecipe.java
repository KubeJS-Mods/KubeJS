package dev.latvian.mods.kubejs.recipe.special;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.recipe.KubeJSRecipeEventHandler;
import dev.latvian.mods.kubejs.recipe.ModifyRecipeResultCallback;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientAction;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ShapelessKubeJSRecipe extends ShapelessRecipe implements KubeJSCraftingRecipe {
	private final List<IngredientAction> ingredientActions;
	private final ModifyRecipeResultCallback modifyResult;
	private final String stage;

	public ShapelessKubeJSRecipe(ShapelessRecipe original, List<IngredientAction> ingredientActions, @Nullable ModifyRecipeResultCallback modifyResult, String stage) {
		super(original.getGroup(), original.category(), original.result, original.getIngredients());
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
	public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
		return kjs$assemble(container, registryAccess);
	}

	public static class SerializerKJS implements RecipeSerializer<ShapelessKubeJSRecipe> {

		// registry replacement-safe(?)
		private static final RecipeSerializer<ShapelessRecipe> SHAPELESS = UtilsJS.cast(RegistryInfo.RECIPE_SERIALIZER.getValue(new ResourceLocation("crafting_shapeless")));

		private static final MapCodec.MapCodecCodec<ShapelessRecipe> SHAPELESS_CODEC = getCodec();

		private static MapCodec.MapCodecCodec<ShapelessRecipe> getCodec() {
			try {
				return UtilsJS.cast(SHAPELESS_CODEC);
			} catch (ClassCastException e) {
				throw new IllegalStateException("Original ShapelessRecipe codec is not a MapCodecCodec!");
			}
		}

		public static final Codec<ShapelessKubeJSRecipe> CODEC = RecordCodecBuilder.create(instance -> {
			return instance.group(
				SHAPELESS_CODEC.codec().forGetter(r -> r),
				IngredientAction.CODEC.listOf().optionalFieldOf("kubejs:actions", List.of()).forGetter(r -> r.ingredientActions),
				ModifyRecipeResultCallback.CODEC.optionalFieldOf("kubejs:modify_result", null).forGetter(r -> r.modifyResult),
				Codec.STRING.optionalFieldOf("kubejs:stage", "").forGetter(r -> r.stage)
			).apply(instance, ShapelessKubeJSRecipe::new);
		});

		@Override
		public Codec<ShapelessKubeJSRecipe> codec() {
			return CODEC;
		}

		@Override
		public ShapelessKubeJSRecipe fromNetwork(FriendlyByteBuf buf) {
			var shapelessRecipe = SHAPELESS.fromNetwork(buf);
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
