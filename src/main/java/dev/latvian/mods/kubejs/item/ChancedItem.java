package dev.latvian.mods.kubejs.item;

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
import net.minecraft.world.item.ItemStack;

public record ChancedItem(ItemStack item, FloatProvider chance) {
	public static final MapCodec<ChancedItem> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ItemStack.CODEC.fieldOf("item").forGetter(ChancedItem::item),
		FloatProvider.CODEC.optionalFieldOf("chance", ConstantFloat.of(1F)).forGetter(ChancedItem::chance)
	).apply(instance, ChancedItem::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ChancedItem> STREAM_CODEC = StreamCodec.composite(
		ItemStack.STREAM_CODEC, ChancedItem::item,
		ByteBufCodecs.fromCodec(FloatProvider.CODEC), ChancedItem::chance,
		ChancedItem::new
	);

	public static final RecipeComponent<ChancedItem> RECIPE_COMPONENT = new SimpleRecipeComponent<>("chanced_item", CODEC.codec(), TypeInfo.of(ChancedItem.class));

	public boolean test(RandomSource random) {
		return random.nextFloat() < chance.sample(random);
	}

	public ItemStack getItemOrEmpty(RandomSource random) {
		return test(random) ? item : ItemStack.EMPTY;
	}

	public ChancedItem withChance(FloatProvider chance) {
		return new ChancedItem(item, chance);
	}
}