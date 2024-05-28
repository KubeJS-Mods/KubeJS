package dev.latvian.mods.kubejs.ingredient;

import com.mojang.serialization.MapCodec;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.IngredientType;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class WildcardIngredient implements KubeJSIngredient {
	public static WildcardIngredient INSTANCE = new WildcardIngredient();

	public static final MapCodec<WildcardIngredient> CODEC = MapCodec.unit(INSTANCE);

	private WildcardIngredient() {
	}

	@Override
	public IngredientType<?> getType() {
		return KubeJSIngredients.WILDCARD.get();
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null;
	}

	@Override
	public Stream<ItemStack> getItems() {
		return ItemStackJS.getList().stream();
	}
}
