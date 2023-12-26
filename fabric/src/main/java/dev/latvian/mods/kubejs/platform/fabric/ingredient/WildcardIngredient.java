package dev.latvian.mods.kubejs.platform.fabric.ingredient;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WildcardIngredient extends KubeJSIngredient {
	public static final WildcardIngredient INSTANCE = new WildcardIngredient();
	public static final KubeJSIngredientSerializer<WildcardIngredient> SERIALIZER = new KubeJSIngredientSerializer<>(KubeJS.id("wildcard"), Codec.unit(INSTANCE), buf -> INSTANCE);
	public static final Ingredient VANILLA_INSTANCE = INSTANCE.toVanilla();

	private WildcardIngredient() {
	}

	@Override
	public KubeJSIngredientSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null;
	}

	@Override
	public List<ItemStack> getMatchingStacks() {
		return ItemStackJS.getList();
	}

	@Override
	public void write(FriendlyByteBuf buf) {
	}
}
