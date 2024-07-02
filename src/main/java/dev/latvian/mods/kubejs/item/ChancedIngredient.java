package dev.latvian.mods.kubejs.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.SimpleRecipeComponent;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.item.crafting.Ingredient;

public record ChancedIngredient(Ingredient ingredient, int count, FloatProvider chance) {
	public static final MapCodec<ChancedIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Ingredient.CODEC.fieldOf("ingredient").forGetter(ChancedIngredient::ingredient),
		Codec.INT.optionalFieldOf("count", 1).forGetter(ChancedIngredient::count),
		FloatProvider.CODEC.optionalFieldOf("chance", ConstantFloat.of(1F)).forGetter(ChancedIngredient::chance)
	).apply(instance, ChancedIngredient::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ChancedIngredient> STREAM_CODEC = StreamCodec.composite(
		Ingredient.CONTENTS_STREAM_CODEC, ChancedIngredient::ingredient,
		ByteBufCodecs.VAR_INT, ChancedIngredient::count,
		ByteBufCodecs.fromCodecWithRegistries(FloatProvider.CODEC), ChancedIngredient::chance,
		ChancedIngredient::new
	);

	public static final RecipeComponent<ChancedIngredient> RECIPE_COMPONENT = new SimpleRecipeComponent<>("chanced_ingredient", CODEC.codec(), TypeInfo.of(ChancedIngredient.class));

	public boolean test(RandomSource random) {
		return random.nextFloat() < chance.sample(random);
	}

	public Ingredient getIngredientOrEmpty(RandomSource random) {
		return test(random) ? ingredient : Ingredient.EMPTY;
	}
}