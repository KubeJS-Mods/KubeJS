package dev.latvian.mods.kubejs.ingredient;

import com.mojang.serialization.MapCodec;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.item.ItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

import java.util.stream.Stream;

public record CreativeTabIngredient(CreativeModeTab tab) implements ICustomIngredient, ItemPredicate {
	public static final MapCodec<CreativeTabIngredient> CODEC = BuiltInRegistries.CREATIVE_MODE_TAB.byNameCodec()
		.fieldOf("tab")
		.xmap(CreativeTabIngredient::new, CreativeTabIngredient::tab);

	public static final StreamCodec<RegistryFriendlyByteBuf, CreativeTabIngredient> STREAM_CODEC = ByteBufCodecs.registry(Registries.CREATIVE_MODE_TAB)
		.map(CreativeTabIngredient::new, CreativeTabIngredient::tab);

	@Override
	public IngredientType<?> getType() {
		return KubeJSIngredients.CREATIVE_TAB.get();
	}

	@Override
	public boolean test(ItemStack stack) {
		return tab.contains(stack);
	}

	@Override
	public Stream<Holder<Item>> items() {
		return tab.getSearchTabDisplayItems()
			.stream()
			.map(ItemStack::typeHolder);
	}

	@Override
	public boolean isSimple() {
		return CommonProperties.get().serverOnly;
	}

	// we do need this override since ICustomIngredient has default false
	// and all of our ingredients are safe
	@Override
	public boolean kjs$canBeUsedForMatching() {
		return true;
	}
}
