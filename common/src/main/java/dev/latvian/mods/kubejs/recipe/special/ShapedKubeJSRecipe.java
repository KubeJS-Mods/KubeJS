package dev.latvian.mods.kubejs.recipe.special;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.recipe.KubeJSRecipeEventHandler;
import dev.latvian.mods.kubejs.recipe.ModifyRecipeResultCallback;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientAction;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.util.UtilsJS;
import it.unimi.dsi.fastutil.chars.CharArraySet;
import it.unimi.dsi.fastutil.chars.CharSet;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ShapedKubeJSRecipe extends ShapedRecipe implements KubeJSCraftingRecipe {

	public static final String SHRINK_KEY = "kubejs:shrink";
	public static final String MIRROR_KEY = "kubejs:mirror";

	public static final MapCodec<ShapedRecipePattern> PATTERN_NO_SHRINK_CODEC = ShapedRecipePattern.Data.MAP_CODEC
		.flatXmap(ShapedKubeJSRecipe::unpackNoShrink,
			pattern -> pattern.data().map(DataResult::success)
				.orElseGet(() -> DataResult.error(() -> "Cannot encode unpacked recipe")));

	public static final MapCodec<ShapedRecipePattern> PATTERN_CODEC = new MapCodec<>() {
		@Override
		public <T> Stream<T> keys(DynamicOps<T> ops) {
			return Stream.concat(ShapedRecipePattern.MAP_CODEC.keys(ops), Stream.of(ops.createString(SHRINK_KEY)));
		}

		@Override
		public <T> DataResult<ShapedRecipePattern> decode(DynamicOps<T> ops, MapLike<T> input) {
			return ops.getBooleanValue(input.get(SHRINK_KEY)).flatMap(shrink -> {
				if (shrink) {
					return ShapedRecipePattern.MAP_CODEC.decode(ops, input);
				} else {
					return PATTERN_NO_SHRINK_CODEC.decode(ops, input);
				}
			});
		}

		@Override
		public <T> RecordBuilder<T> encode(ShapedRecipePattern input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
			return PATTERN_NO_SHRINK_CODEC.encode(input, ops, prefix).add(SHRINK_KEY, ops.createBoolean(false));
		}
	};

	private final boolean mirror;
	private final List<IngredientAction> ingredientActions;
	private final ModifyRecipeResultCallback modifyResult;
	private final String stage;

	public ShapedKubeJSRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result,
							  boolean mirror, List<IngredientAction> ingredientActions, @Nullable ModifyRecipeResultCallback modifyResult, String stage) {
		super(group, category, pattern, result);
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
		for (var i = 0; i <= craftingContainer.getWidth() - pattern.width(); ++i) {
			for (var j = 0; j <= craftingContainer.getHeight() - pattern.height(); ++j) {
				if (mirror && pattern.matches(craftingContainer, i, j, true)) {
					return true;
				}

				if (pattern.matches(craftingContainer, i, j, false)) {
					return true;
				}
			}
		}

		return false;
	}

	public static class SerializerKJS implements RecipeSerializer<ShapedKubeJSRecipe> {

		// registry replacement-safe(?)
		private static final RecipeSerializer<ShapedRecipe> SHAPED = UtilsJS.cast(RegistryInfo.RECIPE_SERIALIZER.getValue(new ResourceLocation("crafting_shaped")));

		// TODO: this is still a bit of a mess
		public static final Codec<ShapedKubeJSRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			// manually copied from the shaped recipe codec
			// (would be nice if we could just swap out specifically the pattern codec from the underlying codec)
			ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter(ShapedRecipe::getGroup),
			CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(ShapedRecipe::category),
			// kubejs modified keys
			PATTERN_CODEC.forGetter(recipe -> recipe.pattern),
			ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("result").forGetter(shapedRecipe -> shapedRecipe.result),
			Codec.BOOL.optionalFieldOf(MIRROR_KEY, true).forGetter(recipe -> recipe.mirror),
			// KubeJS additions
			IngredientAction.CODEC.listOf().optionalFieldOf("kubejs:actions", List.of()).forGetter(r -> r.ingredientActions),
			ModifyRecipeResultCallback.CODEC.optionalFieldOf("kubejs:modify_result", null).forGetter(r -> r.modifyResult),
			Codec.STRING.optionalFieldOf("kubejs:stage", "").forGetter(r -> r.stage)
		).apply(instance, ShapedKubeJSRecipe::new));

		@Override
		public Codec<ShapedKubeJSRecipe> codec() {
			return CODEC;
		}

		@Override
		public ShapedKubeJSRecipe fromNetwork(FriendlyByteBuf buf) {
			var shapedRecipe = SHAPED.fromNetwork(buf);
			var flags = (int) buf.readByte();

			// original values
			var group = shapedRecipe.getGroup();
			var category = shapedRecipe.category();
			var pattern = shapedRecipe.pattern;
			var result = shapedRecipe.result;

			List<IngredientAction> ingredientActions = (flags & RecipeFlags.INGREDIENT_ACTIONS) != 0 ? IngredientAction.readList(buf) : List.of();
			var stage = (flags & RecipeFlags.STAGE) != 0 ? buf.readUtf() : "";
			var mirror = (flags & RecipeFlags.MIRROR) != 0;

			// the pattern can be used as-is because the shrinking logic is done serverside
			// additionally, result modification callbacks do not need to be synced to clients
			return new ShapedKubeJSRecipe(group, category, pattern, result, mirror, ingredientActions, null, stage);
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

	private static DataResult<ShapedRecipePattern> unpackNoShrink(ShapedRecipePattern.Data data) {
		var pattern = data.pattern();
		// we can assume that the pattern is rectangular
		// because the codec has already validated that
		var width = pattern.get(0).length();
		var height = pattern.size();
		NonNullList<Ingredient> nonNullList = NonNullList.withSize(width * height, Ingredient.EMPTY);
		CharSet charSet = new CharArraySet(data.key().keySet());

		for (int row = 0; row < pattern.size(); ++row) {
			String string = pattern.get(row);

			for (int cell = 0; cell < string.length(); ++cell) {
				char symbol = string.charAt(cell);
				Ingredient ingredient = symbol == ' ' ? Ingredient.EMPTY : data.key().get(symbol);
				if (ingredient == null) {
					return DataResult.error(() -> "Pattern references symbol '" + symbol + "' but it's not defined in the key");
				}

				charSet.remove(symbol);
				nonNullList.set(cell + width * row, ingredient);
			}
		}

		return !charSet.isEmpty()
			? DataResult.error(() -> "Key defines symbols that aren't used in pattern: " + charSet)
			: DataResult.success(new ShapedRecipePattern(width, height, nonNullList, Optional.of(data)));
	}

}
