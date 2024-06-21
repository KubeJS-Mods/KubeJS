package dev.latvian.mods.kubejs.recipe.special;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.recipe.KubeJSRecipeSerializers;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientActionHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;

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

	@Override
	public boolean matches(CraftingInput input, Level level) {
		// FIXME: mirror
		return pattern.matches(input);
	}

	public static class SerializerKJS implements RecipeSerializer<ShapedKubeJSRecipe> {

		// TODO: this is still a bit of a mess
		public static final MapCodec<ShapedKubeJSRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			// manually copied from the shaped recipe codec
			// (would be nice if we could just swap out specifically the pattern codec from the underlying codec)
			Codec.STRING.optionalFieldOf("group", "").forGetter(ShapedRecipe::getGroup),
			CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(ShapedRecipe::category),
			// kubejs modified keys
			ShapedRecipePattern.MAP_CODEC.forGetter(recipe -> recipe.pattern),
			ItemStack.STRICT_CODEC.fieldOf("result").forGetter(r -> r.result),
			Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(ShapedRecipe::showNotification),
			// KubeJS additions
			Codec.BOOL.optionalFieldOf(MIRROR_KEY, true).forGetter(ShapedKubeJSRecipe::kjs$getMirror),
			IngredientActionHolder.LIST_CODEC.optionalFieldOf(INGREDIENT_ACTIONS_KEY, List.of()).forGetter(ShapedKubeJSRecipe::kjs$getIngredientActions),
			Codec.STRING.optionalFieldOf(MODIFY_RESULT_KEY, "").forGetter(ShapedKubeJSRecipe::kjs$getModifyResult),
			Codec.STRING.optionalFieldOf(STAGE_KEY, "").forGetter(ShapedKubeJSRecipe::kjs$getStage)
		).apply(instance, ShapedKubeJSRecipe::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, ShapedKubeJSRecipe> STREAM_CODEC = new StreamCodec<>() {
			@Override
			public ShapedKubeJSRecipe decode(RegistryFriendlyByteBuf buf) {
				var group = buf.readUtf();
				var category = buf.readEnum(CraftingBookCategory.class);
				var shapedrecipepattern = ShapedRecipePattern.STREAM_CODEC.decode(buf);
				var result = ItemStack.STREAM_CODEC.decode(buf);
				var showNotification = buf.readBoolean();

				var mirror = buf.readBoolean();
				var ingredientActions = IngredientActionHolder.LIST_STREAM_CODEC.decode(buf);
				var modifyResult = buf.readUtf();
				var stage = buf.readUtf();

				return new ShapedKubeJSRecipe(group, category, shapedrecipepattern, result, showNotification, mirror, ingredientActions, modifyResult, stage);
			}

			@Override
			public void encode(RegistryFriendlyByteBuf buf, ShapedKubeJSRecipe recipe) {
				buf.writeUtf(recipe.getGroup());
				buf.writeEnum(recipe.category());
				ShapedRecipePattern.STREAM_CODEC.encode(buf, recipe.pattern);
				ItemStack.STREAM_CODEC.encode(buf, recipe.result);
				buf.writeBoolean(recipe.showNotification);

				buf.writeBoolean(recipe.kjs$getMirror());
				IngredientActionHolder.LIST_STREAM_CODEC.encode(buf, recipe.kjs$getIngredientActions());
				buf.writeUtf(recipe.kjs$getModifyResult());
				buf.writeUtf(recipe.kjs$getStage());
			}
		};

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
